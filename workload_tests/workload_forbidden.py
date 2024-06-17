from locust import HttpUser, TaskSet, SequentialTaskSet, task, between, constant
import random, json

from utils.votingUtils import VotingUtils

from utils.randomdata import get_sample_user, get_random_image
from utils.dotenv_reader import DotenvReader

env = DotenvReader()

class Delete(SequentialTaskSet):
    wait_time = between(1,4)

    def on_start(self):
        self.register()
    
    def register(self):
        sample_user = get_sample_user()
        self.username = sample_user.get("name")
        print(self.username)
        self.token = VotingUtils.register(self.client, sample_user=sample_user)

    @task
    def getHome(self):
        votings = VotingUtils.get_home(self.client, self.token)
        votings = votings["votings"]
        if len(votings) <= 0:
            raise Exception("getHome: Lista de Votings vazia")
        voting = random.choice(votings)
        self.voting_id_to_delete = voting['id']

    @task
    def deleteVoting(self):
        
        print(f"User {self.username} attempting deletion of voting id -> {self.voting_id_to_delete}.")
        VotingUtils.delete_voting(self.client, self.token, self.voting_id_to_delete)


class DoubleVoting(SequentialTaskSet):
    wait_time = between(1,4)

    @task
    def register(self):
        self.token = VotingUtils.register(self.client)

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


    @task
    def vote_1(self):
        VotingUtils.vote(self.client, self.token, self.selected_voting_id, vote_stat_name="Primeiro voting")
    
    @task
    def vote_2(self): # Suposto dar erro, uma vez que faz um voto duplicado
        VotingUtils.vote(self.client, self.token, self.selected_voting_id, vote_stat_name="Segundo voting")
        


class ExecuteTest(HttpUser):
    tasks = {
        Delete: 1,
        DoubleVoting: 1,
    }
    wait_time = between(1,4)
