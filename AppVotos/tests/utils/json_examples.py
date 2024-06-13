import random
from datetime import datetime, timedelta

def gen_number():
    return random.randint(1, 10_000_000_000)

def get_random_in_range(start: int, end: int):
    return random.randint(start, end)

def gen_random_past_date():
    # Define the start date
    start_date = datetime(1970, 1, 1)
    
    # Get the current date
    end_date = datetime.now()
    
    # Calculate the difference between the start and end dates in days
    date_difference = (end_date - start_date).days
    
    # Generate a random number of days within the range
    random_days = random.randint(0, date_difference)
    
    # Add the random number of days to the start date to get the random date
    random_date = start_date + timedelta(days=random_days)
    
    # Return the random date
    return str(random_date.date())

def get_random_future_datetime():
    """Returns a random future date and time from now to 1 year from now"""
    # Get the current date and time
    now = datetime.now()
    # Define the number of seconds in one year
    one_year_seconds = 60 * 60 * 24 * 365
    # Generate a random number of seconds within the range
    random_seconds = random.randint(0, one_year_seconds)
    # Add the random number of seconds to the current date and time to get the random date and time
    random_date = now + timedelta(seconds=random_seconds)
    # Return the random date and time
    return random_date.strftime('%Y-%m-%d %H:%M:%S')

def now_str():
    """Returns current date and time in format 'YYYY-MM-DD HH:MM:SS'"""
    return datetime.now().strftime('%Y-%m-%d %H:%M:%S')

def get_sample_user():
    """Returns a random user"""
    number = gen_number()
    names = ['Alice', 'Bob', 'Charlie', 'David', 'Eve', 'Frank', 'Grace', 'Hank', 'Ivy', 'Jack', 'Kate', 'Liam', 'Mia', 'Noah', 'Olivia', 'Peter', 'Quinn', 'Rose', 'Sam', 'Tina', 'Uma', 'Vince', 'Wendy', 'Xander', 'Yara', 'Zack']
    surnames = ['Smith', 'Johnson', 'Williams', 'Jones', 'Brown', 'Davis', 'Miller', 'Wilson', 'Moore', 'Taylor', 'Anderson', 'Thomas', 'Jackson', 'White', 'Harris', 'Martin', 'Thompson', 'Garcia', 'Martinez', 'Robinson', 'Clark', 'Rodriguez', 'Lewis', 'Lee', 'Walker']
    name = random.choice(names)
    surname = random.choice(surnames)
    final_name = name + ' ' + surname
    email = f'{name.lower()}.{surname.lower()}_{number}@example.com'
    return {
        "name": final_name,
        "email": email,
        "password": f"user_{number}",
    }

def get_sample_users(quantity: int = 1):
    """Returns a list of random users"""
    return [get_sample_user() for _ in range(quantity)]

def get_sample_option():
    """Returns a random option"""
    number = gen_number()
    return {
        "description": f"option_{number}",
    }
    
def get_sample_options(quantity: int = 1):
    """Returns a list of random options"""
    return [get_sample_option() for _ in range(quantity)]

def get_sample_question():
    number = gen_number()
    options_quantity = get_random_in_range(2, 5)
    return {
        'description': f'question_{number}',
        'options': get_sample_options(options_quantity),
    }

def get_sample_questions(quantity: int = 1):
    """Returns a list of random questions"""
    return [get_sample_question() for _ in range(quantity)]

def get_sample_voting():
    number = gen_number()
    questions_quantity = get_random_in_range(2, 5)
    return {
        'title': f'Voting_{number}',
        'description': f'voting_{number}',
        'enddate': get_random_future_datetime(),
        'questions': get_sample_questions(questions_quantity),
        'privatevoting': random.choice([True, False]),
    }

def get_sample_votings(quantity: int = 1):
    """Returns a list of random votings"""
    return [get_sample_voting() for _ in range(quantity)]