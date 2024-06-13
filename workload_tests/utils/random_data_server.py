import requests
import concurrent.futures
import sys
import types
import json

import randomdata as rd

SERVER = "http://localhost:8080"

def arg_createrandomusers(arguments: list):
    """Inserts random users in server"""
    try:
        if len(arguments) != 3:
            print("Usage: python3 tests.py createrandomusers <number_of_users>")
            number_of_users = 100
            print(f"Using default number of users: {number_of_users}")
        else:
            number_of_users = int(arguments[2])
        users = rd.get_sample_users(number_of_users)

        def create_user(user):
            user_json = json.dumps(user)
            form_data = {
                "user": user_json
            }
            response = requests.post(f"{SERVER}/auth/register", data=form_data)
            if response.status_code == 200:
                print(f"User '{user['name']}' created successfully")
            else:
                print(f"Error creating user '{user['name']}'")
                print(response.text)

        with concurrent.futures.ThreadPoolExecutor(max_workers=8) as executor:
            futures = [executor.submit(create_user, user) for user in users]
            for future in concurrent.futures.as_completed(futures):
                future.result()
        
        with open(f'random_users_{len(users)}.csv', 'w') as f:
            f.write("name,email,password\n")
            for user in users:
                f.write(f"{user['name']},{user['email']},{user['password']}\n")
    except Exception as e:
        print(f"Error: {e}")

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