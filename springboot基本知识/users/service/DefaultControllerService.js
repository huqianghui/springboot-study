'use strict';


/**
 * 删除用户
 * 根据url的id来指定删除对象
 *
 * id Long id
 * returns String
 **/
exports.deleteUserUsingDELETE = function(id) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = "";
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * 通过ID和名字获取单个用户信息
 *
 * name String 用户名
 * userId Long 用户ID
 * returns User
 **/
exports.getUserByIdAndNameUsingGET = function(name,userId) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "name" : "name",
  "id" : 6,
  "age" : 17,
  "email" : "email"
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * 通过ID或者名字获取单个用户信息
 *
 * name String 用户名 (optional)
 * userId Long 用户ID (optional)
 * returns User
 **/
exports.getUserByIdOrNameUsingGET = function(name,userId) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "name" : "name",
  "id" : 6,
  "age" : 17,
  "email" : "email"
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * 获取用户列表
 *
 * returns List
 **/
exports.getUserListUsingGET = function() {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = [ {
  "name" : "name",
  "id" : 6,
  "age" : 17,
  "email" : "email"
}, {
  "name" : "name",
  "id" : 6,
  "age" : 17,
  "email" : "email"
} ];
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * 获取用户详细信息
 * 根据url的id来获取用户详细信息
 *
 * id Long id
 * returns User
 **/
exports.getUserUsingGET = function(id) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "name" : "name",
  "id" : 6,
  "age" : 17,
  "email" : "email"
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * 创建用户
 * 根据User对象创建用户
 *
 * body User user
 * returns String
 **/
exports.postUserUsingPOST = function(body) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = "";
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * 更新用户详细信息
 * 根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息
 *
 * body User user
 * id Long 用户编号
 * returns String
 **/
exports.putUserUsingPUT = function(body,id) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = "";
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}

