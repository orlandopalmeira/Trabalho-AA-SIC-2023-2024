import itertools
from locust import HttpUser, TaskSet, SequentialTaskSet, task, between, constant
import random, json

import utils.csvutils as csvutils
from utils.randomdata import get_sample_user, get_random_image
from utils.dotenv_reader import DotenvReader

from requests.cookies import RequestsCookieJar

env = DotenvReader()

def get_credentials():
    """Returns a list of dictionaries with credentials"""
    csv_file = "random_users_5000.csv"
    users = csvutils.csv_to_dict_list(csv_file)
    users = [{"email": user["email"], "password": user["password"]} for user in users]
    return itertools.cycle(users)

credentials_iter = get_credentials()


class LoginAndVoting(SequentialTaskSet):
    wait_time = constant(2)

    def on_start(self):
        self.voting_id = env.get("VOTING_ID_VOTE_LOADING_TEST")
        self.token = None

    @task
    def login(self):
        credential = next(credentials_iter)
        print("\n!!!!LoginAndVoting!!!!\n")

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

        # É preciso ter este formato para alterar o comportamento de falha
        with self.client.post("/votes", json=vote, cookies={"token": self.token}, catch_response=True) as vote_resp:
            if vote_resp.status_code == 409:
                print("Status code 409 (Conflict). Tentativa de votação dupla, o que não é propriamente falha de carga.")
                # Requests are considered successful if the HTTP response code is OK (<400) -- https://docs.locust.io/en/stable/writing-a-locustfile.html#validating-responses
                vote_resp.success() # Para que não seja tratado como falha


class RegisterAndVoting(SequentialTaskSet):
    wait_time = constant(2)
    
    @task
    def register(self):
        print("\n!!!!RegisterAndVoting!!!!\n")
        self.token = None
        self.sample_user = get_sample_user()
        avatar_path = get_random_image()

        data_form = {
            'user': (None, json.dumps(self.sample_user), 'application/json'),
            'avatar': ('avatar.png', open(avatar_path, 'rb'), 'image/png')
        }

        response = self.client.post("/auth/register", files=data_form)
        # print("Resposta ao Register: ", response.json())
        if response.status_code == 200:
            data = response.json()
            # print("(Success) Json Data: ", data)
            self.token = data['token']
            # print(f"User successfully registered with ID: {data['id']}, Name: {data['name']}, Email: {data['email']}")
        else:
            print("(Failure) Json Data: ", (json.dumps(response, indent=4)))
            raise Exception(f"Failed to register user {self.sample_user['name']}: {response.status_code}")


    @task
    def getHome(self):
        response = self.client.get("/votings", cookies={"token": self.token})
        if response.status_code == 200:
            print("Success in obtaining /votings page")
        else:
            raise Exception("Failure in getting /votings page")
        
        votings = response.json()
        votings = list(filter(lambda voting: not voting['privatevoting'] and voting['enddate']==None , votings))
        
        selected_voting = random.choice(votings)
        self.selected_voting_id = selected_voting['id']
        # print(selected_voting)

    
    @task
    def getStats(self): # Ver estatisticas de uma votação
        v_id = self.selected_voting_id
        response = self.client.get(f"/votings/{v_id}/stats", cookies={"token": self.token}, name="/stats")

        if response.status_code == 200:
            print(f"Success in obtaining statistics from voting-{v_id}.")
        else:
            raise Exception(f"Failure in getting stats, with error {response.status_code}")



class RegisterAndCreateVoting(SequentialTaskSet):

    wait_time = constant(2)
    voting_counter = 0
    
    @task
    def register(self):
        print("\n!!!!RegisterAndCreateVoting!!!!\n")
        self.token = None
        self.sample_user = get_sample_user()
        avatar_path = get_random_image()

        data_form = {
            'user': (None, json.dumps(self.sample_user), 'application/json'),
            'avatar': ('avatar.png', open(avatar_path, 'rb'), 'image/png')
        }

        response = self.client.post("/auth/register", files=data_form)
        # print("Resposta ao Register: ", response.json())
        if response.status_code == 200:
            data = response.json()
            # print("(Success) Json Data: ", data)
            self.token = data['token']
            # print(f"User successfully registered with ID: {data['id']}, Name: {data['name']}, Email: {data['email']}")
        else:
            print("(Failure) Json Data: ", (json.dumps(response, indent=4)))
            raise Exception(f"Failed to register user {self.sample_user['name']}: {response.status_code}")
        

    @task
    def createVoting(self):
        self.voting_counter += 1
        image_name = f'v_image_{self.voting_counter}.png'

        questions = [
            {
                "description": "Locust Generated Question",
                "options": [
                    {"description": "Locust Option 1"}, 
                    {"description": "Locust Option 2"},
                    {"description": "Locust Option 3"},
                    {"description": "Locust Option 4"},
                    {"description": "Locust Option 5"},
                ]
            },
        ]
        data_obj = {
            "title": f"Locust Generated Voting {self.voting_counter}",
            "description": "Basic description generated automatically from Locust",
            "enddate": None,
            "image": image_name,
            "privatevoting": False,
            "privateSelectedUsers": [],
            "showstats": True,
            "showstatsrealtime": True,
            "secretvotes": False,
            "questions": questions
        }

        image_path = get_random_image()
        files = {
            'voting': (None, json.dumps(data_obj), 'application/json'),
            'images': (image_name, open(image_path, 'rb'), 'image/png')
        }

        response = self.client.post("/votings", files=files, cookies={'token': self.token})

        if response.status_code == 200:
            data = response.json()
            print(f"Vote created with ID: {data.get('id')}, Message: {data.get('message')}")
        else:
            print(f"Failed to create vote: {response.status_code}")




class ExecuteTest(HttpUser):
    tasks = {
        # LoginAndVoting,
        # RegisterAndVoting,
        RegisterAndCreateVoting,
    }
    wait_time = constant(1)
