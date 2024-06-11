import itertools
from locust import HttpUser, TaskSet, task, between
import random

import utils.csvutils as csvutils
from utils.dotenv_reader import DotenvReader

env = DotenvReader()

def get_credentials():
    """Returns a list of dictionaries with credentials"""
    users = csvutils.csv_to_dict_list("utils/random_users_100000.csv")
    users = [{"email": user["email"], "password": user["password"]} for user in users]
    return itertools.cycle(users)

credentials_iter = get_credentials()

class VoteLoadingTest(TaskSet):
    def on_start(self):
        self.voting_id = env.get("VOTING_ID_VOTE_LOADING_TEST")

    @task(1)
    def vote(self):
        credential = next(credentials_iter)

        login_response = self.client.post("/auth/login", json=credential)

        if login_response.status_code == 200 and 'set-cookie' in login_response.headers:
            token = login_response.json()['token']
        else:
            print(f"Erro ao fazer login com {credential['name']}.")
            return

        voting = self.client.get(f"/votings/{self.voting_id}", cookies={"token": token}).json()
        vote = {
            "votingid": voting["id"],
            "options": {}
        }
        for question in voting["questions"] :
            vote["options"][question["id"]] = [random.choice(question["options"])["id"]]
        self.client.post("/votes", json=vote, cookies={"token": token})

class ExecuteTest(HttpUser):
    tasks = [VoteLoadingTest]
    wait_time = between(1, 5)