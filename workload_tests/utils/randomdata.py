import random
from datetime import datetime, timedelta

def gen_number():
    return random.randint(1, 10_000_000_000)

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

def get_sample_users(quantity: int):
    """Returns a list of random users"""
    return [get_sample_user() for _ in range(quantity)]