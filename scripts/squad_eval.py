from __future__ import absolute_import
from __future__ import print_function
from __future__ import division
import argparse
from helper import check_file_existence, check_dir_existence
from qa import get_answer
import logging
import time
import json

logFormatter = logging.Formatter("%(asctime)s [%(levelname)-5.5s]  %(message)s")
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('qa_data', type=check_file_existence)
    parser.add_argument('-f', "--log_file", type=str)
    parser.add_argument('-l', "--log_dir", type=check_dir_existence, default='log')

    args = parser.parse_args()

    log_dir = args.log_dir
    log_filename = args.log_file or time.strftime("squad-%Y-%m-%d-%H-%M-%S", time.gmtime())

    fileHandler = logging.FileHandler("{0}/{1}.log".format(log_dir, log_filename))
    fileHandler.setFormatter(logFormatter)
    logger.addHandler(fileHandler)

    consoleHandler = logging.StreamHandler()
    consoleHandler.setFormatter(logFormatter)
    logger.addHandler(consoleHandler)

    count = 0
    correct = 0
    for line in open(args.qa_data):
        count += 1
        data = json.loads(line)
        question = data['question']
        answers = set(data['answer'])
        logger.debug('question: %s' % question)
        logger.debug('answer: %s' % answers)
        result = get_answer(question, logger)
        for answer in answers:
            answer = answer.lower()
            result = result.lower()
            if answer in result or result in answer:
                correct += 1
        print('no:', count, 'correct:', correct)
