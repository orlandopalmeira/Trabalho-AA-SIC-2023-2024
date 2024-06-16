import requests
import random
import sys
import types
import json
from utils.dotenv_reader import DotenvReader
import utils.json_examples as je

SERVER='http://localhost:8080'
env = DotenvReader()
EMAIL = env.get('EMAIL')
PASSWORD = env.get('PASSWORD')

def login(email=EMAIL, password=PASSWORD):
    """Se o login for bem sucedido, retorna o token de autenticação"""
    login_url = f'{SERVER}/auth/login'
    login_data = {'email': email, 'password': password}
    response = requests.post(login_url, json=login_data)
    if response.status_code == 200:
        return response.json()['token']
    else:
        return None

def get_users():
    """Retorna a lista de utilizadores"""
    url = f'{SERVER}/users'
    response = requests.get(url)
    return response.json()

def get_user(user_id):
    """Retorna o utilizador com o id fornecido"""
    users = get_users()
    user = [user for user in users if user['id'] == int(user_id)]
    return user[0] if user else None


#* Funções iniciadas com 'arg_' são funções que são funcionalidades desta script
def arg_login(arguments: list):
    """Test for server login with credentials provided in .env"""
    if len(arguments) not in [2,4]:
        print("Usage: python3 tests.py login [<email> <password>]")
        exit(1)
    email = EMAIL
    password = PASSWORD
    if len(arguments) == 4:
        email = arguments[2]
        password = arguments[3]
    try:
        token = login(email, password)
        if token:
            print(f"Token: {token}")
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_getusers(arguments: list):
    """Test for server get ALL users"""
    try:
        url = f'{SERVER}/users'
        response = requests.get(url)
        print(json.dumps(response.json(), indent=2))
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)
        
def arg_getuser(arguments: list):
    """Test for server get user"""
    if len(arguments) != 3:
        print("Usage: python3 tests.py getuser <user_id>")
        exit(1)

    try:
        url = f'{SERVER}/users/' + arguments[2]
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials") 
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_getuserbyemail(arguments: list):
    """Test for server get user by email"""
    if len(arguments) != 3:
        print("Usage: python3 tests.py getuserbyemail <email_of_user>")
        exit(1)

    try:
        url = f'{SERVER}/users/email/' + arguments[2]
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_createuser(arguments: list):
    """Test for server register user"""
    try:
        url = f'{SERVER}/auth/register'
        user = je.get_sample_user()
        response = requests.post(url, json=user)
        print("Created user:")
        print(json.dumps(response.json(), indent=2))
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_createusers(arguments: list):
    for _ in range(10):
        arg_createuser(arguments)

def arg_getuseraccessiblevotings(arguments: list):
    """Test for server get user accessible votings"""
    try:
        url = f'{SERVER}/votings?alreadyvotedonly=true'
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_getuservotings(arguments: list):
    """Test for server get user votings"""
    try:
        url = f'{SERVER}/votings/user'
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_getuseraccessiblevoting(arguments: list):
    """Test for server get user accessible voting"""
    if len(arguments) != 3:
        print("Usage: python3 tests.py getuseraccessiblevoting <voting_id>")
        exit(1)

    try:
        url = f'{SERVER}/votings/' + arguments[2]
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_createvoting(arguments: list):
    """Test for server create voting"""
    try:
        url = f'{SERVER}/votings'
        token = login()
        if token:
            vote = je.get_sample_voting()
            response = requests.post(url, json=vote, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_createvote(arguments: list):
    """Test for server create vote"""
    try:
        url = f'{SERVER}/votes'
        token = login()
        if token:
            vote = {
                'votingid': '1',
                'options': {
                    '1': [2],
                    '2': [3],
                    '3': [5,7],
                    '4': [10],
                    '5': [11,12,14],
                }
            }
            response = requests.post(url, json=vote, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_getvotingstats(arguments: list):
    """Test for server get voting stats"""
    if len(arguments) != 3:
        print("Usage: python3 tests.py getvotingstats <voting_id>")
        exit(1)

    try:
        url = f'{SERVER}/votings/' + arguments[2] + '/stats'
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token})
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_getuserhistory(arguments: list):
    """Test for server get user history"""
    try:
        url = f'{SERVER}/votings/history'
        token = login()
        if token:
            response = requests.get(url, cookies={'token': token}, params={
                'enddate_start': '2024-06-01 16:00:00',
                'enddate_end': '2024-06-30 16:00:00',
            })
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

if __name__ == "__main__":
    functions_list = [function_name for (function_name,function_) in locals().items() if isinstance(function_, types.FunctionType) and function_name.startswith("arg_")]
    valid_arguments = [function_name.split("_", maxsplit=1)[1] for function_name in functions_list]
    if len(sys.argv) == 1 or sys.argv[1] not in valid_arguments:
        print("Usage: python3 tests.py <arg>")
        print(f"{' '*4}where <arg> is one of the following:")
        for arg in valid_arguments:
            print(f"{' '*8}- '{arg}': {locals()['arg_' + arg].__doc__}")
        exit(1)

    sys.argv[1] = "arg_" + sys.argv[1]
    locals()[sys.argv[1]](sys.argv)