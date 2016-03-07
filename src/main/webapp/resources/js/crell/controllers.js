/**
 * Created by crell on 2016/1/17.
 * Controller
 */
app.controller('RouteMainCtl',['$scope','$location','BusinessService',function($scope,$location,BusinessService){
        $scope.condition = {
            gameTypeCode : 'ACT'
        },
        $scope.businessList = [],
        $scope.currentPage = 1,
        $scope.pages = 1,
        BusinessService.getBusinessList($scope.condition,{pageNo: '1',pageSize: '10'})
        .success(function(obj) {
            $scope.businessList = obj.data;
            $scope.pages = obj.pages;
        });

        $scope.loadMore = function() {
            if ($scope.currentPage < $scope.pages) {
                if ($scope.busy) {
                    return false;
                }
                $scope.busy = true;
                BusinessService.getBusinessList($scope.condition,{pageNo: $scope.currentPage+1,pageSize: '10'})
                .success(function(data){
                    $scope.busy = false;
                    for (var i in data.data) {
                        $scope.businessList.push(data.data[i]);
                    }
                    $scope.currentPage++;
                });
            }
        };
}])
.controller('RouteErrorCtl',function($scope,$http){

})
.controller('RouteLoginCtl',['$scope','$location','LoginService',function($scope,$location,LoginService){
        var form = {};
        $scope.form = form;

        $scope.login = function(isValid){
            if(isValid) {
                LoginService.login($scope.form).then(function(data){
                    if(data.hasLogin){
                        $location.path("my");
                    }else{
                        $scope.loginInfo = data;
                    }
                });
            }else{
                angular.forEach($scope.loginForm,function(e){
                    if(typeof(e) == 'object' && typeof(e.$dirty) == 'boolean'){
                        e.$dirty = true;
                    }
                });
            }
        };
}])
.controller('RouteMyCtl',['$scope','$location','LoginService','UserService',function($scope,$location,LoginService,UserService) {
        UserService.userValid('').then(function(data){

        });

        var loginInfo = LoginService.getLoginInfo();
        $scope.loginInfo = loginInfo;

        $scope.toLogin = function(){
            $location.path('login');
        };

        $scope.toLogoff = function(){
            LoginService.logOff();
        };

}])
.controller('MainController',['$scope','$location','LoginService',function($scope,$location,LoginService) {

        if($.cookie('token') != null){
            LoginService.autoLogin().then(function(data){
                if(!data.hasLogin) $location.path('login');
            });
        }

        $scope.toLogin = function(){
            $location.path('login');
        };

}]);