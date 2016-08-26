<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>changeApp</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1,user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no" name="format-detection"/>
    <link rel="stylesheet" href="/resources/css/weui-jssdk.css">
    <link rel="stylesheet" href="/resources/css/jquery-weui.css">
<style>
    .weui_panel {
        margin-top: 0px;
    }

    .weui_panel_hd {
        font-size: 15px;
    }
</style>
</head>
<body>
<div class="weui_panel weui_panel_access">
    <div class="weui_panel_hd" style="font-size: 17px;color: #353535">上传
    </div>
    <div class="weui_panel weui_panel_access">
        <div class="weui_cell">
            <div class="weui_cell_bd weui_cell_primary">
                <p id="location">你的位置</p>
                <p id="distance">你我距离</p>
                <input type="hidden" value="" name="longitude" id="longitude"/>
                <input type="hidden" value="" name="latitude" id="latitude"/>
            </div>
            <a href="javascript:;" class="weui_btn weui_btn_primary" style="width: 20%;margin-top: 12px;" id="checkMap">查看crell的位置
            </a>
        </div>
        <div class="weui_cell">
            <div class="weui_cell_bd weui_cell_primary">
                <div class="weui_uploader">
                    <div class="weui_uploader_hd weui_cell">
                        <div class="weui_cell_hd">
                            <img src="/resources/images/up.png" alt="icon" style="width:20px;margin-right:5px;display:block">
                        </div>
                        <div class="weui_cell_bd weui_cell_primary">照片</div>
                        <div class="weui_uploader_bd">
                            <c:if test="${photo.img eq null or photo.img eq ''}">
                                <ul class="weui_uploader_files new_image" id="weight_img">
                                </ul>
                            </c:if>
                            <c:if test="${photo.img ne null and photo.img ne ''}">
                                <div class='show_img'><img class='weui_uploader_file'
                                                           src='${photo.img}'/></div>
                            </c:if>
                            <c:if test="${photo.img eq null or photo.img eq ''}">
                                <div class="weui_uploader_input_wrp uploadImg">
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<a href="javascript:;" class="weui_btn weui_btn_primary" style="width: 80%;margin-top: 20px;"
   id="submit">提交
</a>
<a href="javascript:;" onclick="wx.closeWindow();" class="weui_btn weui_btn_plain_default"
   style="width: 80%;margin-top: 20px;margin-bottom: 20px;">关闭</a>
<script src="/resources/js/jquery-2.1.3.min.js"></script>
<script src="/resources/js/jquery-weui.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script>
    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: '${signature.appId}', // 必填，公众号的唯一标识
        timestamp: '${signature.timestamp}', // 必填，生成签名的时间戳
        nonceStr: '${signature.noncestr}', // 必填，生成签名的随机串
        signature: '${signature.signature}',// 必填，签名，见附录1
        jsApiList: [
            'closeWindow',
            'chooseImage',
            'uploadImage',
            'previewImage',
            'getLocation',
            'openLocation'
        ]
    });// 必填，需要使用的JS接口列表，所有JS接口列表见附录2

    //获取位置
    wx.ready(function () {
        //进行经纬度转换为距离的计算
        function Rad(d){
            return d * Math.PI / 180.0;//经纬度转换成三角函数中度分表形式。
        }
        //计算距离，参数分别为第一点的纬度，经度；第二点的纬度，经度
        function GetDistance(lat1,lng1,lat2,lng2){

            var radLat1 = Rad(lat1);
            var radLat2 = Rad(lat2);
            var a = radLat1 - radLat2;
            var  b = Rad(lng1) - Rad(lng2);
            var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                            Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
            s = s *6378.137 ;// EARTH_RADIUS;
            s = Math.round(s * 10000) / 10000; //输出为公里
            //s=s.toFixed(4);
            return s;
        }

        wx.getLocation({
            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function (res) {
                var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                if (latitude == '' || longitude == '') {
                    $.alert("无法获取位置信息,请允许页面获取您的位置信息!");
                    return false;
                } else {
                    $("#latitude").val(latitude);
                    $("#longitude").val(longitude);
                    $("#location").html("你在:" + " 纬度:" + latitude + "经度:" + longitude);

                    var distance = GetDistance(latitude,longitude,31.19857,121.2601);
                    $("#distance").html("你与我相距: " + distance + "km");
                }
            }
        });
    });

    $(document).ready(function () {
        //查看位置
        $("#checkMap").click(function () {
            var latitude = $("#latitude").val(); // 纬度，浮点数，范围为90 ~ -90
            var longitude = $("#longitude").val(); // 经度，浮点数，范围为180 ~ -180。

            wx.openLocation({
                latitude: 31.1965634989,
                longitude: 121.2660648036,
                name: 'E通世界南区',
                address: '上海市青浦区华徐公路685号',
                scale: 14,
                infoUrl: 'http://weixin.qq.com'
            });
        });

        $(".show_img").click(function () {
            var imgurl = $(this).find(".weui_uploader_file")[0].src;
            wx.previewImage({
                current: imgurl,
                urls: [
                    imgurl
                ]
            });
        });

        $(".uploadImg").click(function () {
            var my = $(this);
            var up = my.prev("ul");
            var id = up.attr("id");

            wx.chooseImage({
                count: 1, // 默认9
                sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    my.css("display", "none");
                    var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
//                    alert(localIds);
                    var html = "<li class='show_img'><img class='weui_uploader_file' src='" + localIds + "'/></li>";
                    up.html(html);
                }
            });
        });

        //删除选择的图片
        $(".new_image").on("click", ".show_img", function () {
            var src = $(this);
            var upload = $(this).parent().parent().find(".uploadImg");
            $.confirm("确定要删除此图片?", "删除图片", function () {
                src.remove();
                $("#uploadImg").css("display", "block");
                upload.css("display", "block");
            });
        });
        //提交表单
        $("#submit").click(function () {
            //称重图片
            var hasWeight = ($("#weight_img:has('li')").length) == 0;

            if (hasWeight) {
                $.alert("请上传!", "提示");
                return false;
            }

            var weight_url = '';

            if (!hasWeight) {
                var weight_img = $("#weight_img").find(".weui_uploader_file");
                weight_url = weight_img[0].src;
            }

            $("#submit").attr("disabled", false);
            $.showLoading("上传中...");

            //解决IOS无法上传
            if (weight_url.indexOf("wxlocalresource") != -1) {
                weight_url = weight_url.replace("wxlocalresource", "wxLocalResource");
            }

            //存放微信上传图片ServerID
            var images = {
                weight: []
            };

            $.when($.when(uploadImage(weight_url,"weight"))
                    .done(function(){ ajax(); }));

            function ajax() {
                $.ajax({
                    type: 'POST',
                    url: '/ajax/jssdkTest/upImg',
                    data: {
                        weight: images.weight
                    },
                    dataType: 'json',
                    success: function (data) {
                        if (data.status == '1001') {
                            $.hideLoading();
                            $.toast(data.data + "提交成功");
                            setTimeout(function () {
                                window.location.reload();
                            }, 2000);
                        }
                    },
                    error: function () {
                        $.hideLoading();
                        $.alert("抱歉,提交失败,请稍后重试!", "提示");
                        $("#submit").attr("disabled", false);
                        $("#submit").html("提交");
                    }
                });
            }

            function uploadImage(localId,imageName){
                var dtd = $.Deferred();
                if(localId == ''){
                    dtd.resolve();
                    return dtd.promise();
                }

                wx.uploadImage({
                    localId: localId, // 需要上传的图片的本地ID，由chooseImage接口获得
                    isShowProgressTips: 0, // 默认为1，显示进度提示
                    success: function (res) {
                        var serverId = res.serverId; // 返回图片的服务器端ID
                        images[imageName] = serverId;
                        dtd.resolve();
                    },
                    error: function () {
                        $.hideLoading();
                        $.alert("抱歉,提交失败,请稍后重试!", "提示");
                        $("#submit").attr("disabled", false);
                        $("#submit").html("提交");
                        return false;
                    }
                });

                return dtd.promise();
            }

        });
    });
</script>
</body>
</html>
