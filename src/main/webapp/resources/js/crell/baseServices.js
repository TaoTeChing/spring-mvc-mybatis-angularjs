/**
 * Created by crell on 2016/1/17.
 */
app.factory("BaseService",['$http',function($http){
    return{
        post : function(url,params,page){
            var paramsBody = {};
            paramsBody.token = $.cookie('token');
            paramsBody.random = Math.random();
            paramsBody.body = params;
            paramsBody.page = page;
            return $http.post(url,paramsBody);
        }
    }
}]);
