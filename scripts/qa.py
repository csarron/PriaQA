from __future__ import absolute_import
from __future__ import print_function
from __future__ import division
import argparse
from helper import check_file_existence
import logging

logFormatter = logging.Formatter("%(asctime)s [%(levelname)-5.5s]  %(message)s")
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)

try:
    import urllib.request as url_request
except ImportError:
    import urllib as url_request


try:
    import urllib.parse as url_parse
except ImportError:
    import urllib as url_parse


def get_answer(question_, logger_):
    encoded_question = url_parse.quote_plus(question_)
    with url_request.urlopen('http://localhost:8080?query=%s' % encoded_question) as response:
        result_ = response.read().decode("utf-8").strip()
        logger_.debug('result: %s' % result_)
    return result_


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('questions', nargs='*')
    parser.add_argument('-q', "--question_file", type=check_file_existence)
    args = parser.parse_args()

    consoleHandler = logging.StreamHandler()
    consoleHandler.setFormatter(logFormatter)
    logger.addHandler(consoleHandler)

    questions = args.questions
    question_file = args.question_file
    if questions:
        for question in questions:
            logger.debug('question: %s' % question)
            get_answer(question, logger)
    else:
        with open(question_file, 'r') as f:
            question = f.readline().strip()
            logger.debug('question: %s' % question)
            get_answer(question, logger)
