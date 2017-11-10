from __future__ import absolute_import
from __future__ import print_function
from __future__ import division
import argparse
from helper import check_dir_existence, check_file_existence, regex_match
import json
import logging
from qa import get_answer
import time

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
    log_filename = args.log_file or time.strftime("trec-%Y-%m-%d-%H-%M-%S", time.gmtime())

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
        answer_patterns = set(data['answer'])

        logger.debug('question: %s' % question)
        logger.debug('answer pattern: %s' % answer_patterns)
        t0 = time.time()
        result = get_answer(question, logger)
        is_correct = False
        for answer_pattern in answer_patterns:
            if regex_match(result, answer_pattern):
                correct += 1
                is_correct = True
        t1 = time.time()
        if is_correct:
            status = 'correct'
        else:
            status = 'wrong'
        logger.info('no:%3d,%8s, total:%3d, [time]: %.4f s' % (count, status, correct, t1 - t0))

