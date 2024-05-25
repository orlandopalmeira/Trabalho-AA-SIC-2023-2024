import requests
import sys
import types
import json

SERVER='http://localhost:8080'

def login(email,password):
    """Se o login for bem sucedido, retorna o token de autenticação"""
    login_url = f'{SERVER}/auth/login'
    login_data = {'email': email, 'password': password}
    response = requests.post(login_url, json=login_data)
    if response.status_code == 200:
        return response.cookies['token']
    else:
        return None

#* Funções iniciadas com 'arg_' são funções que são funcionalidades desta script
def arg_login(arguments: list):
    """Test for server login"""
    if len(arguments) != 4:
        print("Usage: python3 tests.py login <email> <password>")
        exit(1)
    try:
        token = login(arguments[2], arguments[3])
        if token:
            print(f"Token: {token}")
        else:
            print("Invalid credentials")
    except Exception as e:
        print(e)

def arg_getuser(arguments: list):
    """Test for server get user"""
    if len(arguments) != 5:
        print("Usage: python3 tests.py getuser <email> <password> <user_id>")
        exit(1)

    url = f'{SERVER}/users/' + arguments[4]
    token = login(arguments[2], arguments[3])
    if token:
        response = requests.get(url, cookies={'token': token})
        print(json.dumps(response.json(), indent=2))
    else:
        print("Invalid credentials") 


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