from unittest import TestCase,main


test_username="testUsername"
test_uid=999999
test_object_uid=12345

test_void_uid=65535
test_void_object_uid=98764

class FirebaseTest(TestCase):
    def test_reading_something(self):
        import firebase
        a=firebase.get_score(test_uid,test_object_uid)
        self.assertNotEqual(a, 0)
        print(a)
    
    def test_reading_nothing(self):
        import firebase
        self.assertEqual(firebase.get_score(test_void_uid,test_object_uid),0)
    
    def test_reading_void_from_person(self):
        import firebase
        self.assertEqual(firebase.get_score(test_uid,test_void_object_uid), 0)

    def test_writing_something(self):
        import random
        import firebase
        obj=random.sample([random.randint(-65536,-1),random.randint(1,65535)],1)[0] ## get -65536 <= num <= 65535 (num != 0)
        firebase.write_score(test_uid,test_object_uid, obj,test_username)
        self.assertEqual(firebase.get_score(test_uid,test_object_uid), obj)
    

if(__name__=="__main__"):
    main()