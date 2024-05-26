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
        return response.cookies['token']
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
        url = f'{SERVER}/users'
        user = je.get_sample_user()
        response = requests.post(url, json=user)
        print("Created user:")
        print(json.dumps(response.json(), indent=2))
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_deleteuser(arguments: list):
    """Test for server delete user"""
    if len(arguments) != 4:
        print("Usage: python3 tests.py deleteuser <user_id> <user_password>")
        exit(1)

    try:
        url = f'{SERVER}/users/' + arguments[2]
        user = get_user(arguments[2])

        token = login(user['email'], arguments[3])
        if token:
            response = requests.delete(url, cookies={'token': token})
            print("Deleted user:")
            print(json.dumps(response.json(), indent=2))
        else:
            print("Invalid credentials")
    except requests.exceptions.ConnectionError:
        print("Provavelmente o servidor aplicacional não está a correr")
    except Exception as e: #* Qualquer outro erro
        print(e)

def arg_updateuser(arguments: list):
    """Test for server update user"""
    if len(arguments) != 4:
        print("Usage: python3 tests.py updateuser <user_id> <user_password>")
        exit(1)

    try:
        url = f'{SERVER}/users/' + arguments[2]
        user = get_user(arguments[2])
        token = login(user['email'], arguments[3])

        field_to_update = random.choice(['name', 'email'])
        print('Field to update:', field_to_update)
        update = {field_to_update: f'{user[field_to_update]}_updated'}
        if token:
            response = requests.put(url, json=update, cookies={'token': token})
            print("Updated user:")
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
        voting = je.get_sample_voting()
        token = login()
        if token:
            response = requests.post(url, json=voting, cookies={'token': token})
            print("Created voting:")
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