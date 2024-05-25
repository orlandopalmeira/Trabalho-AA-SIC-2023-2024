import random

def gen_number():
    return random.randint(1, 10_000_000_000)

def get_random_in_range(start: int, end: int):
    return random.randint(start, end)

def get_sample_user():
    """Returns a random user"""
    number = gen_number()
    return {
        "name": f"User_{number}",
        "email": f"user_{number}@example.com",
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
        'description': f'voting_{number}',
        'questions': get_sample_questions(questions_quantity),
    }

def get_sample_votings(quantity: int = 1):
    """Returns a list of random votings"""
    return [get_sample_voting() for _ in range(quantity)]