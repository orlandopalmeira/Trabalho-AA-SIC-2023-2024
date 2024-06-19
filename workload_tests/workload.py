import itertools
from locust import HttpUser, TaskSet, SequentialTaskSet, task, between, constant
import random, json, os

from utils.votingUtils import VotingUtils

import utils.csvutils as csvutils
from utils.randomdata import get_sample_user, get_random_image
from utils.dotenv_reader import DotenvReader

env = DotenvReader()

def get_credentials():
    """Returns a list of dictionaries with credentials"""
    csv_file = "random_users_10000.csv"
    users = csvutils.csv_to_dict_list(csv_file)
    users = [{"email": user["email"], "password": user["password"]} for user in users]
    return itertools.cycle(users)


class LoginAndCheckStats(SequentialTaskSet):
    wait_time = between(1,5)

    credentials_iter = get_credentials()

    @task
    def login(self):
        credential = next(self.credentials_iter)
        self.token = VotingUtils.login(self.client, credential)

    @task
    def getHome(self):
        response = VotingUtils.get_home(self.client, self.token)
        votings = response.get('votings')

        # Escolhe uma votação de acesso público e com data limite indefinida
        votings = list(filter(lambda voting: not voting['privatevoting'] and voting['enddate'] == None , votings))
        if len(votings) > 0:
            selected_voting = random.choice(votings)
            self.selected_voting_id = selected_voting['id']
            # print(selected_voting)
        else:
            print("AVISO: Não existem votings disponíveis")
            self.interrupt(reschedule=False)

    ## Talvez adicionar um searchTerm query
    
    @task
    def getVoting(self):
        v_id = self.selected_voting_id
        VotingUtils.get_voting(self.client, self.token, v_id)
    
    @task
    def getStats(self): # Ver estatisticas de uma votação
        v_id = self.selected_voting_id
        VotingUtils.see_stats(self.client, self.token, v_id)


class RegisterAndVoting(SequentialTaskSet):
    wait_time = between(1,5)

    def on_start(self):
        self.voting_id = env.get("VOTING_ID_VOTE_LOADING_TEST")
    
    @task
    def register(self):
        # print("\n!!!!  2  !!!!\n")
        self.token = VotingUtils.register(self.client)

    @task
    def getHome(self):
        VotingUtils.get_home(self.client, self.token)

    @task
    def getVotingAndPostVoting(self):
        VotingUtils.vote(self.client, self.token, self.voting_id)


voting_counter = 0

class RegisterOnceAndCreateVoting(SequentialTaskSet):
    wait_time = between(1,3)

    def on_start(self):
        self.register()
    
    def register(self):
        self.token = VotingUtils.register(self.client)

    @task
    def getHome(self):
        VotingUtils.get_home(self.client, self.token)

    @task
    def createVoting(self):
        # print("\n!!!!  3  !!!!\n")
        global voting_counter

        voting_counter += 1
        self.created_vote_id = VotingUtils.create_voting(self.client, self.token, voting_counter)

    @task
    def getVoting(self):
        VotingUtils.get_voting(self.client, self.token, self.created_vote_id)

    ## Caso se decida fazer com que se veja estatísticas tbm
    # @task
    # def getStats(self): 
    #     v_id = self.selected_voting_id
    #     VotingUtils.see_stats(self.client, self.token, v_id)



class ExecuteTest(HttpUser):

    ### TASK_SET=3 locust -f workload.py -u 50 -r 10 --host=http://localhost:8080 --headless
    task_set_mapping = {
        "1": LoginAndCheckStats,
        "2": RegisterAndVoting,
        "3": RegisterOnceAndCreateVoting
    }

    selected_task_set = os.getenv('TASKS')
    print("selected_task_set: ", selected_task_set)
    if selected_task_set and selected_task_set in task_set_mapping:
        tasks = {task_set_mapping[selected_task_set]: 1}
    else:
        tasks = {
            LoginAndCheckStats: 1,
            RegisterAndVoting: 1,
            RegisterOnceAndCreateVoting: 1,
        }
    print("Tasks: ", tasks)

    wait_time = between(1,5)
