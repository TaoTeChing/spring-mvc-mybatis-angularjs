/**
 * Created by crell on 2016/1/17.
 */
app.factory("BaseService",['$http',function($http){
    return{
        post : function(url,params){
            var paramsBody = {};
            paramsBody.random = Math.random();
            paramsBody.timestamp = new Date();
            paramsBody.body = params;
            return $http.post(url,paramsBody);
        },
        delete : function(url,params){
            var paramsBody = {};
            paramsBody.random = Math.random();
            paramsBody.timestamp = new Date();
            paramsBody.body = params;
            return $http.post(url,paramsBody);
        },
        put : function(url,params){
            var paramsBody = {};
            paramsBody.random = Math.random();
            paramsBody.timestamp = new Date();
            paramsBody.body = params;
            return $http.put(url,paramsBody);
        },
        get : function(url,params){
            return $http.get(url,{params : params});
        }
    }
}]);
