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

        var loginInfo = LoginService.getLoginInfo();
        $scope.loginInfo = loginInfo;

        $scope.toLogin = function(){
            $location.path('login');
        };

        $scope.toLogoff = function(){
            LoginService.logOff();
        };

}])
.controller('RouteRegisterCtl',['$scope','RegisterService',function($scope,RegisterService){

    $scope.register = function(){
        if($scope.checkMatch()) {
            RegisterService.register($scope.form).then(function(obj) {
                if(obj.data.status == Status.SUCCESS){
                    var loginInfo = obj.data.data;

                    $.cookie('userName',loginInfo.userName,{expires: 7});
                    $.cookie('nickName',loginInfo.nickName,{expires: 7});
                    location.href = '/my';
                }
            });
        }else{
            angular.forEach($scope.registerForm,function(e){
                if(typeof(e) == 'object' && typeof(e.$dirty) == 'boolean'){
                    e.$dirty = true;
                }
            });
        }
    }

    $scope.validExists = function(){
        if($scope.form.userName){
            RegisterService.remoteValid($scope.form.userName).then(function(data) {
                if(data.status == Status.SUCCESS){
                    return true;
                }
            });
        }
        return false;
    };

    $scope.checkMatch = function(){
        var form = $scope.form;
        if(form.password && form.passwordConfirm ){
            if(form.password != form.passwordConfirm){
                return false;
            }
        }
        return true;
    }

    $scope.changeCaptcha = function($event) {//生成验证码
        $event.target.src = '/ajax/captcha-image?' + Math.floor(Math.random()*100);
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