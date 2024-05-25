import random

def get_sample_user():
    """Returns a random user"""
    number = random.randint(1, 10_000_000_000)
    return {
        "name": f"User_{number}",
        "email": f"user_{number}@example.com",
        "password": f"user_{number}",
    }
    