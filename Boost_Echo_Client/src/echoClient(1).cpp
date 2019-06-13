#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>

using namespace std;

bool shouldTerminate;
bool shouldStopReading;

class KeyboardTask {
private:
    ConnectionHandler *connectionHandler;

public:
    KeyboardTask(ConnectionHandler *connectionHandler1) : connectionHandler(connectionHandler1) {}

    void run() {
        while (!shouldTerminate) {
            if (!shouldStopReading) {
                const short bufsize=1024;
                char buf[bufsize];
                cin.getline(buf, bufsize);
                string line(buf);
                int index=line.find_first_of(" ");
                string mOp=line.substr(0, index);
                line=line.substr(index + 1);
                short opcode;
                char bytes[2];
                if (mOp == "REGISTER") {
                    opcode=1;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    index=line.find_first_of(" ");
                    string userName=line.substr(0, index);
                    if (!connectionHandler->sendLine(userName))
                        break;
                    line=line.substr(index + 1);
                    string password=line.substr(0, index);
                    if (!connectionHandler->sendLine(password))
                        break;
                } else if (mOp == "LOGIN") {
                    opcode=2;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    index=line.find_first_of(" ");
                    string userName=line.substr(0, index);
                    if (!connectionHandler->sendLine(userName))
                        break;
                    line=line.substr(index + 1);
                    string password=line.substr(0, index);
                    if (!connectionHandler->sendLine(password))
                        break;
                } else if (mOp == "LOGOUT") {
                    shouldStopReading=true;
                    opcode=3;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                } else if (mOp == "FOLLOW") {
                    opcode=4;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    index=line.find_first_of(" ");
                    string followUnfollow=line.substr(0, index);
                    char bytesArr[1];
                    if (followUnfollow == "0") {
                        bytesArr[0]='0';
                        if (!connectionHandler->sendBytes(bytesArr, 1))
                            break;
                    } else {
                        bytesArr[0]='1';
                        if (!connectionHandler->sendBytes(bytesArr, 1))
                            break;
                    }
                    line=line.substr(index + 1);
                    index=line.find_first_of(" ");
                    string numOfUsers=line.substr(0, index);
                    connectionHandler->shortToBytes((short) stoi(numOfUsers), bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    line=line.substr(index + 1);
                    while (line.size() != 0) {
                        index=line.find_first_of(" ");
                        if (index == -1) {
                            string nextUserName=line;
                            if (!connectionHandler->sendLine(nextUserName))
                                break;
                            line="";
                        }
                        else {
                            string nextUserName=line.substr(0, index);
                            if (!connectionHandler->sendLine(nextUserName))
                                break;
                            line=line.substr(index + 1);
                        }
                    }
                } else if (mOp == "POST") {
                    opcode=5;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    string content=line;
                    if (!connectionHandler->sendLine(content))
                        break;
                } else if (mOp == "PM") {
                    opcode=6;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    index=line.find_first_of(" ");
                    string userName=line.substr(0, index);
                    if (!connectionHandler->sendLine(userName))
                        break;
                    line=line.substr(index + 1);
                    string content=line;
                    if (!connectionHandler->sendLine(content))
                        break;
                } else if (mOp == "USERLIST") {
                    opcode=7;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                } else if (mOp == "STAT") {
                    opcode=8;
                    connectionHandler->shortToBytes(opcode, bytes);
                    if (!connectionHandler->sendBytes(bytes, 2))
                        break;
                    index=line.find_first_of(" ");
                    string userName=line.substr(0, index);
                    if (!connectionHandler->sendLine(userName))
                        break;
                }
            }
        }
    }
};
class ScreenTask {
private:
    ConnectionHandler *connectionHandler;
public:
    ScreenTask(ConnectionHandler *connectionHandler1) : connectionHandler(connectionHandler1) {}

    void run() {
        while (!shouldTerminate) {
            char chars[2];
            if (!connectionHandler->getBytes(chars, 2))
                break;
            short opcode=connectionHandler->bytesToShort(chars);
            string output;
            if (opcode == 9) {
                output="NOTIFICATION ";
                char temp[1];
                if (!connectionHandler->getBytes(temp, 1))
                    break;
                if (temp[0] == '0')
                    output.append("PM");
                else
                    output.append("Public");
                string temp2="";
                for (int i=0; i < 2; i++) {
                    if (!connectionHandler->getLine(temp2))
                        break;
                    output+=" " + temp2;
                    temp2="";
                }
            } else if (opcode == 10) {
                output="ACK ";
                char chars[2];
                if (!connectionHandler->getBytes(chars, 2))
                    break;
                short messageOpcode=connectionHandler->bytesToShort(chars);
                output+= to_string(messageOpcode);
                if (messageOpcode == 3) {
                    shouldTerminate=true;
                }
                else if (messageOpcode == 4 || messageOpcode == 7) {
                    char temp3[2];
                    if (!connectionHandler->getBytes(temp3, 2))
                        break;
                    short numOfUsers=connectionHandler->bytesToShort(temp3);
                    output+=" " + to_string(numOfUsers);
                    string user;
                    for (int i=0; i < numOfUsers; i++) {
                        if (!connectionHandler->getLine(user)) {
                            break;
                        }
                        output+=" " + user;
                        user="";
                    }
                }
                else if (messageOpcode == 8) {
                    char temp[2];
                    if (!connectionHandler->getBytes(temp, 2))
                        break;
                    short numOfPosts=connectionHandler->bytesToShort(temp);
                    output+= " " + to_string(numOfPosts);
                    if (!connectionHandler->getBytes(temp, 2))
                        break;
                    short numOfFollowers=connectionHandler->bytesToShort(temp);
                    output+= " " + to_string(numOfFollowers);
                    if (!connectionHandler->getBytes(temp, 2))
                        break;
                    short numOfFollowing=connectionHandler->bytesToShort(temp);
                    output+= " " + to_string(numOfFollowing);
                }
            } else if (opcode == 11) {
                output="ERROR ";
                char chars[2];
                if (!connectionHandler->getBytes(chars, 2))
                    break;
                short ansToMsg = connectionHandler->bytesToShort(chars);
                if (ansToMsg==3)
                    shouldStopReading=false;
                output+=to_string(ansToMsg);
            }
            cout << output << endl;
        }
    }
};


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }

    string host = argv[1];
    short port = static_cast<short>(atoi(argv[2]));

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        cerr << "Cannot connect to " << host << ":" << port << endl;
        return 1;
    }

    KeyboardTask keyboardTask(&connectionHandler);
    ScreenTask screenTask(&connectionHandler);
    thread thread1(&KeyboardTask::run,&keyboardTask);
	thread thread2(&ScreenTask::run,&screenTask);
	thread1.join();
	thread2.join();
    return 0;
}
