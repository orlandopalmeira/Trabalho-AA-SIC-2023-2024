import itertools
from locust import HttpUser, TaskSet, SequentialTaskSet, task, between, constant
import random, json

from utils.votingUtils import VotingUtils

import utils.csvutils as csvutils
from utils.randomdata import get_sample_user, get_random_image
from utils.dotenv_reader import DotenvReader

env = DotenvReader()

def get_credentials():
    """Returns a list of dictionaries with credentials"""
    csv_file = "random_users_5000.csv"
    users = csvutils.csv_to_dict_list(csv_file)
    users = [{"email": user["email"], "password": user["password"]} for user in users]
    return itertools.cycle(users)


class LoginAndVoting(SequentialTaskSet):
    wait_time = between(1,5)

    credentials_iter = get_credentials()

    def on_start(self):
        self.voting_id = env.get("VOTING_ID_VOTE_LOADING_TEST")
        self.token = None

    @task
    def login(self):
        credential = next(self.credentials_iter)
        # print("\n!!!!  1  !!!!\n")

        self.token = VotingUtils.login(self.client, credential)

    @task
    def getVotingAndPostVoting(self):
        VotingUtils.vote(self.client, self.token, self.voting_id)


class RegisterAndVoting(SequentialTaskSet):
    wait_time = between(1,5)
    
    @task
    def register(self):
        # print("\n!!!!  2  !!!!\n")
        self.token = VotingUtils.register(self.client)


    @task
    def getHome(self):

        response = VotingUtils.get_home(self.client, self.token)
        votings = response.get('votings')
        print(votings)

        votings = list(filter(lambda voting: not voting['privatevoting'] and voting['enddate'] == None , votings))
        if len(votings) == 0:
            selected_voting = random.choice(votings)
            self.selected_voting_id = selected_voting['id']
            # print(selected_voting)
        else:
            raise Exception("Não existem votings disponíveis")

    
    @task
    def getStats(self): # Ver estatisticas de uma votação
        v_id = self.selected_voting_id
        VotingUtils.see_stats(self.client, self.token, v_id)
        


class RegisterAndCreateVoting(SequentialTaskSet):
    wait_time = between(1,5)

    voting_counter = 0

    def on_start(self):
        self.register()
    
    def register(self):
        self.token = VotingUtils.register(self.client)

    @task
    def createVoting(self):
        # print("\n!!!!  3  !!!!\n")

        self.voting_counter += 1
        created_vote_id = VotingUtils.create_voting(self.client, self.token, self.voting_counter)
        




class ExecuteTest(HttpUser):
    tasks = {
        # LoginAndVoting: 1,
        # RegisterAndVoting: 1,
        RegisterAndCreateVoting: 1,
    }
    wait_time = between(1,5)
