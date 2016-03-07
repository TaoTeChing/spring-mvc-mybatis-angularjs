/**
 * Created by crell on 2016/1/17.
 */

var app = angular.module('mainApp', ['ngRoute'])

/*****
 * Interceptor
 */
.factory('statusInterceptor', ['$q','$location',function($q,$location) {
    var statusInterceptor = {
        response: function(response) {
            var deferred = $q.defer();
            if(response.data.status == Status.ERROR){//系统错误
                $location.path('/error');
                return deferred.promise;
            }else if(response.data.status == Status.FAILED){
                alert(response.data.msg);
                return deferred.promise;
            }else if(response.data.status == Status.INVALID_USER){
                $location.path('/login');
                return deferred.promise;
            }else{
                return response;
            }
        }
    };
    return statusInterceptor;
}])
/****
 *路由 模板设置
 */
.config(['$routeProvider','$locationProvider','$httpProvider', function ($routeProvider,$locationProvider,$httpProvider) {
    $locationProvider.html5Mode(true);

    $httpProvider.interceptors.push('statusInterceptor');

    $routeProvider
        .when('/', {
            templateUrl: 'view/main.html',
            controller: 'RouteMainCtl'
        })
        .when('/error', {
            templateUrl: 'view/error.html',
            controller: 'RouteErrorCtl'
        })
        .when('/login', {
            templateUrl: 'view/login.html',
            controller: 'RouteLoginCtl'
        })
        .when('/my', {
            templateUrl: 'view/my.html',
            controller: 'RouteMyCtl'
        })
        .otherwise({
            redirectTo: '/'
        });
}]);

app.directive('whenScrolled', function() {
    return function(scope, element, attr) {
        var raw = element[0];
        element.bind('scroll', function() {
            if (raw.scrollTop+raw.offsetHeight >= raw.scrollHeight) {
                scope.$apply(attr.whenScrolled);
            }
        });
    };
});
