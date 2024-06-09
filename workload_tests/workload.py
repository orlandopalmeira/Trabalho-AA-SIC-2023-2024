import itertools
from locust import HttpUser, TaskSet, task, between

import utils.csvutils as csvutils

def get_credentials():
    """Returns a list of dictionaries with credentials"""
    users = csvutils.csv_to_dict_list("utils/random_users_100000.csv")
    users = [{"email": user["email"], "password": user["password"]} for user in users]
    return itertools.cycle(users)

credentials_iter = get_credentials()

class UserBehavior(TaskSet):
    def on_start(self):
        # Seleciona credenciais exclusivas para cada utilizador virtual
        self.credential = next(credentials_iter)
        
        # Fazer login e obter o cookie de autenticação
        response = self.client.post("/auth/login", json=self.credential)
        
        # Verifica se o login foi bem-sucedido e o cookie de autenticação foi recebido
        if response.status_code == 200 and 'set-cookie' in response.headers:
            self.token = response.json()['token']
        else:
            print(f"Erro ao fazer login com {self.credential['name']}.")

    @task(1)
    def get_voting_stats(self):
        self.client.get("/votings/1/stats", cookies={"token": self.token})

class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(1, 5)