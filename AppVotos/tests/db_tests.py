import sys
import types
import mysql.connector #* pip install mysql-connector-python

from utils.dotenv_reader import DotenvReader

env = DotenvReader()

# Connect to the database
cnx = mysql.connector.connect(
    host=env.get("DB_HOST"),
    user=env.get("DB_USER"),
    password=env.get("DB_PASSWORD"),
    database=env.get("DB_NAME")
)

def arg_getusers(arguments: list):
    """Test for mysql get ALL users"""
    # Create a cursor object to execute queries
    cursor = cnx.cursor()

    # Execute a query
    query = "SELECT * FROM users"
    cursor.execute(query)

    # Fetch all rows from the result set
    rows = cursor.fetchall()

    # Process the rows
    for row in rows:
        print(row)

    # Close the cursor
    cursor.close()

def arg_getuser(arguments: list):
    """Test for mysql get user"""
    if len(arguments) != 3:
        print("Usage: python3 db_tests.py getuser <user_id>")
        exit(1)

    # Create a cursor object to execute queries
    cursor = cnx.cursor()

    # Execute a query
    query = "SELECT * FROM users WHERE id = %s"
    cursor.execute(query, (arguments[2],))

    # Fetch all rows from the result set
    rows = cursor.fetchall()

    # Process the rows
    for row in rows:
        print(row)

    # Close the cursor
    cursor.close()

def arg_getuserbyemail(arguments: list):
    """Test for mysql get user by email"""
    if len(arguments) != 3:
        print("Usage: python3 db_tests.py getuserbyemail <email_of_user>")
        exit(1)

    # Create a cursor object to execute queries
    cursor = cnx.cursor()

    # Execute a query
    query = "SELECT * FROM users WHERE email = %s"
    cursor.execute(query, (arguments[2],))

    # Fetch all rows from the result set
    rows = cursor.fetchall()

    # Process the rows
    for row in rows:
        print(row)

    # Close the cursor
    cursor.close()

def arg_deleteuser(arguments: list):
    """Test for mysql delete user"""
    if len(arguments) != 3:
        print("Usage: python3 db_tests.py deleteuser <user_id>")
        exit(1)

    # Create a cursor object to execute queries
    cursor = cnx.cursor()

    # Execute a query
    query = "DELETE FROM users WHERE id = %s"
    cursor.execute(query, (arguments[2],))

    # Commit the changes
    cnx.commit()

    # Close the cursor
    cursor.close()

def arg_setcurrentidautoincrementusers(arguments: list):
    """Set current id auto increment users"""
    if len(arguments) != 3:
        print("Usage: python3 db_tests.py setcurrentidautoincrementusers <new_start_id_autoincrement>")
        exit(1)

    # Create a cursor object to execute queries
    cursor = cnx.cursor()

    # Execute a query
    query = f"ALTER TABLE users AUTO_INCREMENT={arguments[2]};"
    cursor.execute(query)

    # Commit the changes
    cnx.commit()

    # Close the cursor
    cursor.close()
    print("Current id auto increment setted successfully")



if __name__ == "__main__":
    functions_list = [function_name for (function_name,function_) in locals().items() if isinstance(function_, types.FunctionType) and function_name.startswith("arg_")]
    valid_arguments = [function_name.split("_", maxsplit=1)[1] for function_name in functions_list]
    if len(sys.argv) == 1 or sys.argv[1] not in valid_arguments:
        print("Usage: python3 db_tests.py <arg>")
        print(f"{' '*4}where <arg> is one of the following:")
        for arg in valid_arguments:
            print(f"{' '*8}- '{arg}': {locals()['arg_' + arg].__doc__}")
        exit(1)

    sys.argv[1] = "arg_" + sys.argv[1]
    locals()[sys.argv[1]](sys.argv)
    cnx.close()