import utils.json_examples as je
import json

if __name__ == '__main__':
    id_ = 1
    data = je.get_sample_votings(50)
    for d in data:
        d['id'] = id_
        d['image'] = None
        del d['questions']
        id_ += 1
    with open('votos.json', 'w') as f:
        json.dump(data, f, indent=4)
    print('votos.json created')