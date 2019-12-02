'use strict';

var utils = require('../utils/writer.js');
var DefaultController = require('../service/DefaultControllerService');

module.exports.deleteUserUsingDELETE = function deleteUserUsingDELETE (req, res, next) {
  var id = req.swagger.params['id'].value;
  DefaultController.deleteUserUsingDELETE(id)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUserByIdAndNameUsingGET = function getUserByIdAndNameUsingGET (req, res, next) {
  var name = req.swagger.params['name'].value;
  var userId = req.swagger.params['userId'].value;
  DefaultController.getUserByIdAndNameUsingGET(name,userId)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUserByIdOrNameUsingGET = function getUserByIdOrNameUsingGET (req, res, next) {
  var name = req.swagger.params['name'].value;
  var userId = req.swagger.params['userId'].value;
  DefaultController.getUserByIdOrNameUsingGET(name,userId)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUserListUsingGET = function getUserListUsingGET (req, res, next) {
  DefaultController.getUserListUsingGET()
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUserUsingGET = function getUserUsingGET (req, res, next) {
  var id = req.swagger.params['id'].value;
  DefaultController.getUserUsingGET(id)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.postUserUsingPOST = function postUserUsingPOST (req, res, next) {
  var body = req.swagger.params['body'].value;
  DefaultController.postUserUsingPOST(body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.putUserUsingPUT = function putUserUsingPUT (req, res, next) {
  var body = req.swagger.params['body'].value;
  var id = req.swagger.params['id'].value;
  DefaultController.putUserUsingPUT(body,id)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};
