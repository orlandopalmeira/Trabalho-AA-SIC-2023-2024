import itertools
from locust import HttpUser, TaskSet, SequentialTaskSet, task, between
import random, json

import utils.csvutils as csvutils
from utils.randomdata import get_sample_user, get_random_image
from utils.dotenv_reader import DotenvReader

from requests.cookies import RequestsCookieJar

env = DotenvReader()

def get_credentials():
    """Returns a list of dictionaries with credentials"""
    csv_file = "random_users_500.csv"
    users = csvutils.csv_to_dict_list(csv_file)
    users = [{"email": user["email"], "password": user["password"]} for user in users]
    return itertools.cycle(users)

credentials_iter = get_credentials()


class LoginAndVoting(SequentialTaskSet):
    wait_time = between(1, 3)
    def on_start(self):
        self.voting_id = env.get("VOTING_ID_VOTE_LOADING_TEST")
        self.token = None

    @task
    def login(self):
        credential = next(credentials_iter)
        # email = credential["email"]
        # print(email)

        login_response = self.client.post("/auth/login", json=credential)

        if login_response.status_code == 200 and 'set-cookie' in login_response.headers:
            self.token = login_response.json()['token']
        else:
            print(f"Failed to register user: {login_response.status_code}")
            raise Exception(f"Failed to register user: {login_response.status_code}")

    @task
    def getVotingAndPostVoting(self):
        voting = self.client.get(f"/votings/{self.voting_id}", cookies={"token": self.token})
        if voting.status_code != 200:
            print(f"Voting Failed with status code {voting.status_code}")
            raise Exception(f"Voting Failed with status code {voting.status_code}")
        
        voting = voting.json()
        vote = {
            "votingid": voting["id"],
            "options": {}
        }
        for question in voting["questions"] :
            vote["options"][question["id"]] = [random.choice(question["options"])["id"]]

        vote_resp = self.client.post("/votes", json=vote, cookies={"token": self.token})
        if vote_resp.status_code == 409:
            print("Status code 409 (Conflict). Tentativa de votação dupla, o que não é propriamente falha de carga.")
            # Requests are considered successful if the HTTP response code is OK (<400) -- https://docs.locust.io/en/stable/writing-a-locustfile.html#validating-responses
            vote_resp.success() # Para que não seja tratado como falha


class RegisterAndLoginAndVoting(SequentialTaskSet):
    wait_time = between(3, 6)
    
    @task
    def register(self):
        self.sample_user = get_sample_user()
        avatar_path = get_random_image("images/")

        files = {
            'user': (None, json.dumps(self.sample_user), 'application/json'),
            'avatar': ('avatar.png', open(avatar_path, 'rb'), 'image/png')
        }

        response = self.client.post("/auth/register", files=files)

        if response.status_code == 200:
            data = response.json()
            print(f"User registered with ID: {data.id}, Name: {data.name}, Email: {data.email}")
        else:
            raise Exception(f"Failed to register user: {response.status_code}")


    @task
    def login(self):
        credential = {
            "email": self.sample_user.email,
            "password": self.sample_user.password
        }

        login_response = self.client.post("/auth/login", json=credential)

        if login_response.status_code == 200 and 'set-cookie' in login_response.headers:
            self.token = login_response.json()['token']
        else:
            login_response.failure(f"Erro ao fazer login com {credential.email}.\nResponse: {login_response}")

    @task
    def getHome(self):
        self.client.get("/votings")



class ExecuteTest(HttpUser):
    tasks = [
        LoginAndVoting,
        # RegisterAndLoginAndVoting,
    ]
    wait_time = between(1, 5)



