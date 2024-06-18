import random, json

from utils.randomdata import get_sample_user, get_random_image


class VotingUtils:
    @staticmethod
    def login(client, credential):
        """Retorna um token de autenticação."""
        response = client.post("/auth/login", json=credential)
        if response.status_code == 200 and 'set-cookie' in response.headers:
            return response.json()['token']
        else:
            raise Exception(f"Failed to login: {response.status_code}")

    @staticmethod
    def register(client, sample_user=None):
        """Retorna um token de autenticação."""
        if sample_user == None:
            sample_user = get_sample_user()
            # print(sample_user)

        ## Simula uma probabilidade do utilizador querer inserir foto de perfil ou não o querer.
        avatar_value = None
        if random.choice([True, False]):
            avatar_path = get_random_image()
            avatar_value = ('avatar.png', open(avatar_path, 'rb'), 'image/png')
        data_form = {
            'user': (None, json.dumps(sample_user), 'application/json'),
            'avatar': avatar_value
        }
        response = client.post("/auth/register", files=data_form)
        if response.status_code == 200:
            return response.json()['token']
        else:
            raise Exception(f"Erro: {response.status_code}. Failed to register user {sample_user['name']}.")
        
    @staticmethod
    def get_home(client, token):
        response = client.get("/votings?orderBy=enddate&order=desc&page=1&votings_per_page=8", cookies={"token": token}, name = "/votings")
        if response.status_code == 200:
            # print("Success in obtaining /votings page")
            pass
        else:
            raise Exception("Failure in getting /votings page")
        
        votings = response.json()
        return votings


    @staticmethod
    def get_voting(client, token, voting_id):
        voting_response = client.get(f"/votings/{voting_id}", cookies={"token": token}, name="/votings/{id}")
        if voting_response.status_code != 200:
            raise Exception(f"Erro {voting_response.status_code}. Failed to get voting-{voting_id}.")
        
        voting = voting_response.json()
        return voting


    @staticmethod
    def vote(client, token, voting_id, vote_stat_name="/votings/{id}"):
        
        voting = VotingUtils.get_voting(client, token, voting_id)
        
        vote = {
            "votingid": voting["id"],
            "options": {}
        }
        for question in voting["questions"] :
            vote["options"][question["id"]] = [random.choice(question["options"])["id"]]

        client.post("/votes", json=vote, cookies={"token": token}, name = vote_stat_name)

        ## Para caso quisesse interpretar double voting como o calhar
        # with client.post("/votes", json=vote, cookies={"token": token}, catch_response=True) as vote_resp:
        #     if vote_resp.status_code == 409:
        #         print("Status code 409 (Conflict). Tentativa de votação dupla, o que não é propriamente falha de carga.")
        #         # Requests are considered successful if the HTTP response code is OK (<400) -- https://docs.locust.io/en/stable/writing-a-locustfile.html#validating-responses
        #         vote_resp.success()

    @staticmethod
    def create_voting(client, token, voting_counter):
        """Retorna o id do voto criado."""
        image_name = f'v_image_{voting_counter}.png'

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
            "title": f"Locust Generated Voting {voting_counter}",
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

        response = client.post("/votings", files=files, cookies={'token': token})

        if response.status_code == 200:
            data = response.json()
            # print(f"Vote created with ID: {data.get('id')}, Message: {data.get('message')}")
            return data.get('id')
        else:
            print(f"Failed to create vote: {response.status_code}")

    @staticmethod
    def delete_voting(client, token, voting_id):
        response = client.delete(f"/votings/{voting_id}", cookies={'token': token}, name="/votings/{id}" )
        if response.status_code == 200:
            data = response.json()
            print(f"Deleted voting with ID: {data.get('id')}, Message: {data.get('message')}")
            return data.get('id')
        else:
            print(f"Delete Voting: Erro {response.status_code} {(response.text)}")

    @staticmethod
    def see_stats(client, token, voting_id):
        """Carregar informação das estatísticas de uma votação"""
        response = client.get(f"/votings/{voting_id}/stats", cookies={"token": token}, name="votings/{id}/stats")

        if response.status_code == 200:
            # print(f"Success in obtaining statistics from voting-{voting_id}.")
            pass
        else:
            raise Exception(f"Failure in getting stats, with error {response.status_code}")
