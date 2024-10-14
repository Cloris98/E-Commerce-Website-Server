package com.mmall.service.impl;

import com.mmall.common.Constant;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("The username does not exist");
        }

        // Password MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("Incorrect username or password");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("Login Success", user);
    }


    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.checkValid(user.getUsername(), Constant.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Constant.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Constant.Role.ROLE_CUSTOMER);
        // MD5 encryption
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("Failed to create new user");
        }
        return ServerResponse.createBySuccess("Register Success");
    }


    public ServerResponse<String> checkValid(String str, String type) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            // start to check if it's valid or not
            if(Constant.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("Username is already taken");
                }
            }
            if(Constant.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("Email is already taken");
                }
            }
        } else{
            return ServerResponse.createByErrorMessage("The parameter type can not be empty");
        }

        return ServerResponse.createBySuccess("Account is valid");
    }


    public ServerResponse selectQuestion(String username) {

        ServerResponse validResponse = this.checkValid(username, Constant.USERNAME);
        if(validResponse.isSuccess()){
            // user does not exist
            return ServerResponse.createByErrorMessage("User does not exist");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("The question of retrieving the password is empty");
    }


    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            // answer is correct for this user
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("The answer is incorrect");
    }


    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("Parameter forgetToken can not be empty");
        }

        ServerResponse validResponse = this.checkValid(username, Constant.USERNAME);
        if(validResponse.isSuccess()){
            // user does not exist
            return ServerResponse.createByErrorMessage("User does not exist");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("Token not valid");
        }

        if(org.apache.commons.lang3.StringUtils.equals(forgetToken, token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("Update Password Success");
            }
        }else{
            return ServerResponse.createByErrorMessage("Token incorrect, please check your token");
        }

        return ServerResponse.createByErrorMessage("Reset password fail");
    }


    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        // Prevent horizontal overreach, check the old password for current user
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("The old password is incorrect");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("Reset Password Success");
        }
        return ServerResponse.createByErrorMessage("Reset password fail");
    }


    public ServerResponse<User> updateInformation(User user){
        // cannot update username

        // need to check email address, if new email already exist? and if the email already existed, the email cannot
        // be used by current user

        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("Email is already taken");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("User update success",user);
        }
        return ServerResponse.createByErrorMessage("User update fail");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("User not found");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    // backend
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Constant.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }




}
