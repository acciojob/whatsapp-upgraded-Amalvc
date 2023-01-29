package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {
  HashMap<String,User>user1=new HashMap<>();
  HashMap<Group,List<User>>group1=new HashMap<>();
  int count=0;
  int messageCount=0;
  List<Message>messageList=new ArrayList<>();

  HashMap<Group,List<Message>>groupMessage=new HashMap<>();
  HashMap<User,List<Message>>userListHashMap=new HashMap<>();


  public void createUser(String name,String mobile)throws Exception{
    if(user1.containsKey(mobile)){
      throw new Exception("User already exists");
    }
    User user=new User(name,mobile);
    user1.put(mobile,user);
  }
  public Group createGroup(List<User> user){
    if(user.size()==2){
      Group group=new Group(user.get(1).getName(),2);
      group1.put(group,user);
      return group;

    }
    count++;
    String ans="Group "+count;
    Group group=new Group(ans,user.size());
    group1.put(group,user);
    return group;
  }
  public int createMessage(String content){
    Message message=new Message(++messageCount,content);
    message.setTimestamp(new Date());
    messageList.add(message);
    return messageCount;
  }
  public int sendMessage(Message message, User sender, Group group) throws Exception{
    if(!group1.containsKey(group)){
      throw new Exception("Group does not exist");
    }

    boolean flag=false;
    for(User u:group1.get(group)){
      if(u.equals(sender)){
        flag=true;
        break;
      }
    }


    if(flag==false){
      throw new Exception("You are not allowed to send message");
    }


    if(groupMessage.containsKey(group)){
      groupMessage.get(group).add(message);
    }
    else{
      List<Message>list=new ArrayList<>();
      list.add(message);
      groupMessage.put(group,list);
    }

    if(userListHashMap.containsKey(sender)){
      userListHashMap.get(sender).add(message);
    }
    else{
      List<Message>messageList1=new ArrayList<>();
      messageList1.add(message);
      userListHashMap.put(sender,messageList1);
    }
    return groupMessage.get(group).size();
  }

  public void changeAdmin(User approver, User user, Group group) throws Exception{
     if(!group1.containsKey(group)){
       throw new Exception("Group does not exist");
     }
    boolean flag=false;
    for(User u:group1.get(group)){
      if(u.equals(user)){
        flag=true;
        break;
      }
    }
    if(!flag){
      throw new Exception("User is not a participant");
    }
    User admin=group1.get(group).get(0);
    if(!approver.equals(admin)){
      throw new Exception("Approver does not have rights");
    }
    User newAdmin=null;
    Iterator<User>iterator=group1.get(group).iterator();
    while(iterator.hasNext()){
      User user2=iterator.next();
      if(user2.equals(user)){
        newAdmin=user2;
        iterator.remove();
      }
    }
    group1.get(group).add(0,newAdmin);

  }
  public int removeUser(User user) throws Exception{

    boolean flag=false;
    Group g=null;
    for(Group group:group1.keySet()){
      for(User u:group1.get(group)){
        if(u.equals(user)){
          flag=true;
          g=group;
          break;
        }
      }
    }
    if(flag==false){
      throw  new Exception("User not found");
    }
    if(group1.get(g).get(0).equals(user)){
      throw new Exception("Cannot remove admin");
    }
    List<Message>userMssg=userListHashMap.get(user);
    for(Group group:groupMessage.keySet()){
      for(Message message:groupMessage.get(group)){
        if(userMssg.contains(message)){
          groupMessage.get(group).remove(message);
        }
      }
    }
    for(Message message:messageList){
      if(userMssg.contains(message)){
        messageList.remove(message);
      }
    }
    group1.get(g).remove(user);
    userListHashMap.remove(user);
    return group1.get(g).size()+groupMessage.get(g).size()+messageList.size();
  }

}
