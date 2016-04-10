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
.controller('RouteBusinessCtl',['$scope','$routeParams','BusinessService','OrderService','CartService',
        function($scope,$routeParams,BusinessService,OrderService,CartService){
        BusinessService.getBusiness($routeParams.id)
        .success(function(obj) {
             $scope.business = obj.data;
        });

        $scope.buy = function(id){
            OrderService.buy(id)
            .success(function(obj) {
                location.href = '/order';
            });
        };

        $scope.addCart = function(id){
            CartService.addCart(id)
            .success(function(obj) {
                alert('添加成功');
            });
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
.controller('RouteMyCtl',['$scope','$location','LoginService',function($scope,$location,LoginService) {

        var loginInfo = LoginService.getLoginInfo();
        $scope.loginInfo = loginInfo;

        $scope.toLogin = function(){
            $location.path('login');
        };

        $scope.toLogoff = function(){
            LoginService.logOff();
        };

        $scope.myCart = function(){
            $location.path('cart');
        };
}])
.controller('RouteCartCtl',['$scope','CartService',function($scope,CartService) {
        CartService.myCart()
        .success(function(obj) {
            $scope.cartList = obj.data;
        });

        $scope.selected = [];
        $scope.cartBalance = 0;
        $scope.cartSelect = function(target){
            if(target.classList.length == 1){
                var isSel = true;
                if($scope.cartList.length == $scope.selected.length) isSel = false;
                angular.forEach($scope.cartList,function(data){
                    $scope["isSel"+data.business.businessId] = isSel;
                    if(isSel && $scope.selected.indexOf(data.business.businessId) == -1) $scope.selected.push(data.business.businessId);
                    if(!isSel) $scope.selected = [];
                });
                sumBalance(true,isSel);
            }else{
                var id = parseInt(target.children[0].innerText);
                if($scope.selected.indexOf(id) == -1){
                    $scope.selected.push(id);
                    $scope["isSel"+id] = true;
                    sumBalance(id,true);
                }else{
                    var index = $scope.selected.indexOf(id);
                    $scope.selected.splice(index,1);
                    $scope["isSel"+id] = false;
                    sumBalance(id);
                }
            }
        };

        var sumBalance = function(id,plus){
            var price = 0;
            if(typeof id == 'boolean' && plus) $scope.cartBalance = 0;
            angular.forEach($scope.cartList,function(data){
                if(typeof id == 'boolean'){
                    price = price + data.business.price * data.num;
                }else{
                    if(data.business.businessId == id){
                        price = data.business.price * data.num;
                    }
                }
            });
            $scope.cartBalance = plus?$scope.cartBalance + price : $scope.cartBalance - price;
        };

        $scope.goBalance = function(){
            if($scope.selected.length == 0){
                alert("请选择商品");
                return;
            }
            location.href = '/order';
        };
}])
.controller('RouteRegisterCtl',['$scope','RegisterService',function($scope,RegisterService){

    $scope.register = function(){
        if($scope.checkMatch()) {
            RegisterService.register($scope.form).then(function(obj) {
                if(obj.data.status == Status.SUCCESS){
                    var loginInfo = obj.data.data;

                    $.cookie('userName',loginInfo.userName,{expires: 1});
                    $.cookie('nickName',loginInfo.nickName,{expires: 1});
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