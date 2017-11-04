import argparse
import os
import re


def check_file_existence(file_):
    if not os.path.exists(file_):
        raise argparse.ArgumentTypeError("%s does not exist!" % file_)
    # print("using graph file:", graph_file_)
    return file_


def check_dir_existence(dir_):
    if not os.path.exists(dir_):
        os.makedirs(dir_)
    return dir_


def regex_match(text, pattern):
    """Test if a regex pattern is contained within a text."""
    try:
        pattern = re.compile(pattern, flags=re.IGNORECASE + re.UNICODE + re.MULTILINE,)
    except BaseException as e:
        print(e)
        return False
    return pattern.search(text) is not None
