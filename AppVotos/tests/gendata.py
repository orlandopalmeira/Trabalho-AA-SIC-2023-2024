import utils.json_examples as je
from datetime import datetime
import json

if __name__ == '__main__':
    id_ = 1
    data = je.get_sample_votings(50)
    for d in data:
        d['id'] = id_
        d['image'] = None
        d['creationdate'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        d['status'] = 'active'
        del d['questions']
        id_ += 1
    with open('votos.json', 'w') as f:
        json.dump(data, f, indent=4)
    print('votos.json created')