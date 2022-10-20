from unittest import TestCase, main
from base64 import b64decode

test_username = "testUsername"
test_uid = 999999
test_object_uid = 12345

test_void_uid = 65535
test_void_object_uid = 98764

phishing_link = b64decode(
    "aHR0cHM6Ly90ZXN0c2FmZWJyb3dzaW5nLmFwcHNwb3QuY29tL3MvcGhpc2hpbmcuaHRtbA=="
).decode("utf-8")

url_testcase = [
    "https://gitpod.com",
    "dsfgqrwefdsf.re",
    "dsf.ewq sdfa.bdre",
    "fdg.poknbvghnmkjhv.w",
]


class FirebaseScoreTest(TestCase):
    def test_reading_something(self):
        from src.modules import firebase

        a = firebase.get_score(test_uid, test_object_uid)
        self.assertNotEqual(a, 0)
        print(a)

    def test_reading_nothing(self):
        from src.modules import firebase

        self.assertEqual(firebase.get_score(test_void_uid, test_object_uid), 0)

    def test_reading_void_from_person(self):
        from src.modules import firebase

        self.assertEqual(firebase.get_score(test_uid, test_void_object_uid), 0)

    def test_writing_something(self):
        import random
        from src.modules import firebase

        obj = random.sample([random.randint(-100, -1), random.randint(1, 100)], 1)[0]
        # get -100 <= num <= 100 (num != 0)
        firebase.write_score(test_uid, test_object_uid, obj, test_username)
        self.assertEqual(firebase.get_score(test_uid, test_object_uid), obj)


class FirebaseCommandsTest(TestCase):
    def test_reading_command(self):
        from src.modules import firebase

        commands = firebase.read_commands(test_uid)
        self.assertNotEqual({}, commands)
        return commands

    def test_writing_command(self):
        from src.modules import firebase
        import random

        command = "_test_" + str(+random.randint(0, 65535))
        response = "test:" + str(random.randint(0, 65535))
        firebase.write_command(test_uid, command, response)
        self.assertDictContainsSubset(
            {command: response}, firebase.read_commands(test_uid)
        )
        return command

    def test_deleting_command(self):
        from src.modules import firebase

        command = self.test_writing_command()
        firebase.delete_command(test_uid, command)
        self.assertNotIn(command, firebase.read_commands(test_uid))

    @classmethod
    def tearDownClass(cls):
        # flushing test commands
        from src.modules import firebase

        commands = firebase.read_commands(test_uid)
        for i in commands:
            if i.startswith("_test_"):
                firebase.delete_command(test_uid, i)


class SafetyBrowsingTest(TestCase):
    def test_phishing_lookup(cls):
        import asyncio
        from commands import safebrowsing as sb

        res = asyncio.run(sb.lookup(phishing_link))
        cls.assertNotEqual(res, None)

    def test_url_parsing(cls):
        from commands import safebrowsing as sb

        for i in url_testcase:
            cls.assertNotEqual(sb.url_regex.search(i), None)

    def test_get_firebase_status(cls):
        from modules import firebase

        cls.assertEqual(firebase.is_safesbowsing_enabled(test_uid), True)

    def test_write_firebase_status(cls):
        from modules import firebase
        firebase.set_safetybrowsing(test_uid, False)
        cls.assertEqual(firebase.is_safesbowsing_enabled(test_uid), False)
    
    @classmethod
    def tearDownClass(cls):
        from modules import firebase
        firebase.set_safetybrowsing(test_uid, True)

if __name__ == "__main__":
    main()
