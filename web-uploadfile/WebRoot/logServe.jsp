<%@ page language="java" contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String type = request.getParameter("type")==null?"":request.getParameter("type");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/share/json2.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/interface/BasicInfoDwr.js'></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/widget/jquery/jquery-1.3.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/widget/datepicker/WdatePicker.js"></script>
<script type="text/javascript" src="js/jquery.jplayer.min.js"></script>
<%@include file="../../ui/theme/include.jsp"%>
<title>log服务</title>
<style type="text/css">
 #deviceTableBody td{
	border-width:0px 0px 1px 0px;
	border-style: solid;
	border-color:#6699FF;
	line-height:30px;
 }
 #showContent .label{
 	text-align:right;
 	width:46%;
 }
 #showContent .value{
 	text-align:left;
 }
 #showContent input{
 	border: 1px solid #a5b8bd;
 	margin:1px 2px;
 }
</style>
</head>
<script type="text/javascript">
var basePath = '<%=basePath%>';
var type = "<%=type%>";
var ipsearch ="";
var keywordsearch = "";
var startTimeSearch = "";
var endTimeSearch = "";
var pageindex = 1;
var pagesize = 30;//加载一次显示条数
var condIndex = -1;
var flag = false;//是否继续滚屏动作
var closeRefreshId = null;
var realFalg = false;

//颜色设置
function setColor(obj){
	var jsonObj = JSON.parse(obj);
	if(jsonObj!=null&&jsonObj.length>0){
		for ( var i = 0; i < jsonObj.length; i++) {
			//var array_element = jsonObj[i];//id、level、desc、color
			
		}
	}
}

//初始化数据   实时数据
function initlog(){
	$("#loadImg").show();
	BasicInfoDwr.getRellog(reallog);
	closeRefreshId = setInterval(function(){
		$("#loadImg").show();
		BasicInfoDwr.getRellog(reallog);
	},10000);				
}

function reallog(data){
	var arry=JSON.parse(data);
	var playSoundFile = arry[arry.length-1].playSoundFile;
	playSound(playSoundFile);
	var htmltemp = "";
	if(arry != null && arry.length>1){
		for(var i=0;i<arry.length-1;i++){
			htmltemp += "<tr ";
			if(arry[i].fcolor != "#FFFFFF")
				htmltemp += " style='background-color:"+arry[i].fcolor+"'";
			htmltemp += "><td>"+arry[i].fdesc+"</td><td>"+arry[i].ip+"</td><td>"+arry[i].time+"</td><td>"+arry[i].log+"</td></tr>";
		}
		if(!realFalg){
			$("#deviceTableBody").html(htmltemp);
			$("#loadImg").hide();
		}
	}else{
		$("#deviceTableBody").html('<tr><td colspan=4 algin="center">当前实时日志条数为零！</td></tr>');
	}
}
	
//历史日志查询
function searchData(data){
	if(data == null){
		var failmessage = "与Syslog服务通信失败！";
		$("#deviceTableBody").html("<tr><td colspan=4 algin='center'>"+failmessage+"</td></tr>");
	}else{
		var condData = JSON.parse(data);
		condIndex = condData.index;
		var searchLog = condData.result;
		var htmltemp = "";
		if(condData != null && searchLog.length>1){
			if(searchLog.length == pagesize){//初始pageindex = 1,当查询获取的数据条数等于pagesize时展示“点击加载更多”
				for ( var j = 0; j < searchLog.length; j++) {
					htmltemp += "<tr ";
					if(searchLog[j].fcolor != "#FFFFFF")
						htmltemp += " style='background-color:"+searchLog[j].fcolor+"'";
					htmltemp += "><td>"+searchLog[j].fdesc+"</td><td>"+searchLog[j].ip+"</td><td>"+searchLog[j].time+"</td><td>"+searchLog[j].log+"</td></tr>";
				}
				$("#deviceTableBody").html(htmltemp);
				pageindex ++;
				$("#deviceTableBody").append("<tr id='moreLoad'><td colspan='4'>点击加载更多</td></tr>");
			}else{//初始pageindex=1，当查询获取的数据条数不等于【即小于pagesize】时，不展示“点击加载更多”
				for ( var j = 0; j < searchLog.length; j++) {
					htmltemp += "<tr ";
					if(searchLog[j].fcolor != "#FFFFFF")
						htmltemp += " style='background-color:"+searchLog[j].fcolor+"'";
					htmltemp += "><td>"+searchLog[j].fdesc+"</td><td>"+searchLog[j].ip+"</td><td>"+searchLog[j].time+"</td><td>"+searchLog[j].log+"</td></tr>";
				}
				$("#deviceTableBody").html(htmltemp);
			}
		}else{
			$("#deviceTableBody").html("<tr><td colspan=4 algin='center'>当前条件下日志条数为零！</td></tr>");
		}
	}
	$("#loadImg").hide();
}

//点击加载更多
function clickMoreData(data){
	var moredata= JSON.parse(data);
	condIndex = moredata.index;
	var moreLog = moredata.result;
	var htmltemp = "";
	if(pagesize == moreLog.length){//pageindex = 2，等于2*pagesize时添加scrollData()函数
		for ( var j = 0; j < moreLog.length; j++) {
			htmltemp += "<tr ";
			if(moreLog[j].fcolor != "#FFFFFF")
				htmltemp += " style='background-color:"+moreLog[j].fcolor+"'";
			htmltemp += "><td>"+moreLog[j].fdesc+"</td><td>"+moreLog[j].ip+"</td><td>"+moreLog[j].time+"</td><td>"+moreLog[j].log+"</td></tr>";
		}
		$("#deviceTableBody").append(htmltemp);
		pageindex ++;//第三页开始滚动窗口来加载
		flag = true;
		scrollData();
	}else{//不等于2*pagesize[即小于2pagesize]时展示查询数据即可
		for ( var j = 0; j < moreLog.length; j++) {
			htmltemp += "<tr ";
			if(moreLog[j].fcolor != "#FFFFFF")
				htmltemp += " style='background-color:"+moreLog[j].fcolor+"'";
			htmltemp += "><td>"+moreLog[j].fdesc+"</td><td>"+moreLog[j].ip+"</td><td>"+moreLog[j].time+"</td><td>"+moreLog[j].log+"</td></tr>";
		}
		$("#deviceTableBody").append(htmltemp);
	}
	$("#loadImg").hide();
}

//窗口滚动加载
function scrollData(){
	$("#logdataId").scroll(function() {
		if(flag && ($(this).scrollTop() >= $(this)[0].scrollHeight - $(this).height())){
			dwr.engine.setAsync(false);//设置同步
			//alert(ipsearch+","+keywordsearch+","+startTimeSearch+","+endTimeSearch+","+condIndex+","+pageindex+","+pagesize);
			$("#loadImg").show();
			BasicInfoDwr.getHislog(ipsearch,keywordsearch,startTimeSearch,endTimeSearch,condIndex,pageindex,pagesize,function(data){
				var scrolldata= JSON.parse(data);
		    	condIndex = scrolldata.index;
		    	var scrollLog = scrolldata.result;//alert("滚一次获取数据量="+scrollLog.length);
				if(scrollLog.length == pagesize){
					var htmltemp = "";
		    		for ( var j = 0; j < scrollLog.length; j++) {
		    			htmltemp += "<tr ";
		    			if(scrollLog[j].fcolor != "#FFFFFF")
		    				htmltemp += " style='background-color:"+scrollLog[j].fcolor+"'";
						htmltemp += "><td>"+scrollLog[j].fdesc+"</td><td>"+scrollLog[j].ip+"</td><td>"+scrollLog[j].time+"</td><td>"+scrollLog[j].log+"</td></tr>";
					}
					$("#deviceTableBody").append(htmltemp);
					pageindex ++;
					flag = true;
				}else{//小于展示所有
					var htmltemp2 = "";		
		    		for ( var j = 0; j < scrollLog.length; j++) {
		    			htmltemp2 += "<tr ";
		    			if(scrollLog[j].fcolor != "#FFFFFF")
		    				htmltemp2 += " style='background-color:"+scrollLog[j].fcolor+"'";
						htmltemp2 += "><td>"+scrollLog[j].fdesc+"</td><td>"+scrollLog[j].ip+"</td><td>"+scrollLog[j].time+"</td><td>"+scrollLog[j].log+"</td></tr>";
					}
					$("#deviceTableBody").append(htmltemp2);
					flag= false;
				}
			});
			$("#loadImg").hide();
			dwr.engine.setAsync(true); //设置到初始状态
		}
	});
}

//IP验证     搜索的时候要使用到
function IPMatch(ip){
	var regip = /^(([0-1]?\d{1,2}|2[0-4]\d|25[0-5])\.){3}([0-1]?\d{1,2}|2[0-4]\d|25[0-5])$/;//laill 正确的ip地址格式
	flag_ip = regip.test(ip);
	if (!flag_ip){
		alert("请输入正确的ip地址");
		return false;
	}
	return true;
}
	 
function autoSize(){
	var reheight=document.documentElement.clientHeight;
	$("#mainDiv").css('height',reheight-31);//高度自适应
}

function playSound(fileName){
	showPlayer(basePath+"syslog/event"+fileName);
}
function showPlayer(url){
	 $("#jquery_jplayer_1").jPlayer("setMedia", {
        mp3: url // Defines the m4v url
     }).jPlayer("play");
}

	$(function(){
		if(type =="udphistory"){//历史日志
		   realFalg = true;
		   window.clearInterval(closeRefreshId);
		   $("#loadImg").hide();
		   $("#deviceTableBody tr").remove();
		   $("#logTitle").html("历史日志");
		   $("#hideImg").show();
		   //$("#search").hide();
		   //$("#realLog").show();
		   $("#showContent").show();
		   $("#logdataId").css("height",(document.documentElement.clientHeight)-31-144);
		}else{//实时日志
			BasicInfoDwr.getColorCfg(setColor);
			initlog();
		}
		
		$("#jquery_jplayer_1").jPlayer( {
		    supplied: "mp3",
		    swfPath: "js"
	    });
		
	   /*$("#search").click(function(){//	切换历史查询
		   realFalg = true;
		   window.clearInterval(closeRefreshId);
		   $("#loadImg").hide();
		   $("#deviceTableBody tr").remove();
		   $("#logTitle").html("历史日志");
		   $("#hideImg").show();
		   $("#search").hide();
		   $("#realLog").show();
		   $("#showContent").show();
		   $("#logdataId").css("height",$("#mainDiv").height()-128);
	   });
	   $("#realLog").click(function(){//切换实时日志
		   realFalg = false;
		   if($("#showContent").is(':hidden')){
			   $("#hideImg").click();
			   $("#hideImg").attr("src","../../img/icon/nav_up.gif").attr("title","隐藏查询条件面板");
		   }
		   $("#showContent").hide();
		   
		   $("#deviceTableBody tr").remove();
		   $("#logTitle").html("实时日志"); 
		   $("#hideImg").hide();
		   $("#search").show();
		   $("#realLog").hide();
		   $("#logdataId").css("height",$("#mainDiv").height());
		   initlog();
	   });*/
	   
	   $("#hideImg").toggle(function(){
			$(this).attr("src","../../img/icon/nav_down.gif");
			$(this).attr("title","展开查询条件面板");
			$("#logdataId").css("height",$("#mainDiv").height());
			$("#showContent").slideUp("normal");
		},function(){		
			$(this).attr("src","../../img/icon/nav_up.gif");
			$(this).attr("title","隐藏查询条件面板");
			$("#showContent").slideDown("normal");
			$("#logdataId").css("height",$("#mainDiv").height()-144);
		});
		
		//根据条件搜索
		$("#startSearch").on("click",function(){
			flag = false;
			pageindex = 1;
		    condIndex = -1;
			ipsearch = $.trim($("#ipCond").val());
			keywordsearch = $.trim($("#keyworkCond").val());
			var datecond = $.trim($("#dateCond").val());//日期
			var starttime = $.trim($("#starttimeCond").val())=="" ? "00:00:00" : $.trim($("#starttimeCond").val())+":00";
			var endtime = $.trim($("#endtimeCond").val())=="" ? "23:59:59" : $.trim($("#endtimeCond").val())+":00";
			if(ipsearch == ""){
				alert("ip地址不能为空");
				return false;
			}else{
				if(!IPMatch(ipsearch)){							
					return false;
				}
			}
			if(datecond == ""){
				alert("查询日期不能为空");
				return false;
			}else{
				if(starttime>endtime){
					alert("结束时间不能小于开始时间！");return false;
				}
				startTimeSearch = datecond + " "+ starttime;
				endTimeSearch = datecond + " "+ endtime;
			}
			$("#loadImg").show();
			BasicInfoDwr.getHislog(ipsearch,keywordsearch,startTimeSearch,endTimeSearch,condIndex,pageindex,pagesize,searchData);
		});
		
		//"点击加载更多"  查询数据
		$(document).on("click","#moreLoad",function(){
			$("#moreLoad").remove();
			$("#loadImg").show();
			//alert(ipsearch+","+keywordsearch+","+startTimeSearch+","+endTimeSearch+","+condIndex+","+pageindex+","+pagesize);
			BasicInfoDwr.getHislog(ipsearch,keywordsearch,startTimeSearch,endTimeSearch,condIndex,pageindex,pagesize,clickMoreData);
		});
});
</script>
<body class="sTransBG0" onload="autoSize();" onresize="autoSize()">
	<div class="mian" style="margin:1px 10px 1px 10px;">
		<div class="sTransWBG40" style="height:25px;padding:2px 0;">
			<p id="logTitle" style="font-size: 17px;display: inline-block;margin: 2px 5px 0px 15px;font-weight: bold;">实时日志</p>
			<img id="loadImg" style="display:none;" src="../../img/icon/wait.gif"   />
			<!-- <div class="" style="float:right;margin-right:30px;">
				<input type="button" class="sButton6" id="search" style="cursor:pointer;" value="历史日志查询" title="历史日志查询"/>
				<input type="button" class="sButton4" id="realLog" style="cursor:pointer;display:none;" value="实时日志" title="实时日志"/>
			</div> -->
		</div>
		<div id="mainDiv" style="overflow: hidden;">
			<div id='showContent' style='display:none;'>
				<table class='condition simple' style='width:100%;margin-bottom: 4px;'>
					<tr>
						<td class='label'>IP地址：</td>
						<td class='value'><input class='searchText' type='text' style="height:20px;" id='ipCond' size='20' value='' /><span style="color: red;"> *</span></td>
				    </tr>
					<tr  class='alt'>
						<td class='label'>查询日期：</td>
						<td class='value'>
							<input name="dateCond" type="text" style="margin:1px 2px;" class="Wdate searchText" id="dateCond" onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd'})" readonly/>
	   					    <span style="color: red;"> *</span>
						</td>
					</tr>
					<tr>
						<td class='label'>精确时间：</td>
						<td class='value'>
							<input name="starttimeCond" type="text" style="width:66px;margin:1px 2px;" class="Wdate searchText" id="starttimeCond" onfocus="WdatePicker({skin:'whyGreen',dateFmt:'HH:mm'})" readonly/> -
	   					    <input name="endtimeCond" type="text" style="width:67px;margin:1px 2px;" class="Wdate searchText" id="endtimeCond" onfocus="WdatePicker({skin:'whyGreen',dateFmt:'HH:mm'})" readonly/>
						</td>
					</tr>
					<tr class='alt'>
						<td class='label'>关键字：</td>
						<td class='value'><input class='searchText' type='text' style="height:20px;" id='keyworkCond' size='20' value='' /></td>
					</tr>
					<tr align="center" style="height: 35px;border-top:1px solid #888888;">
						<td colspan="2">
							<input type="button" id="startSearch" class="sButton2" value="查询" title="查询" style="cursor:pointer;"/>&nbsp
						</td>
					</tr>
				</table>
			</div>
			<div style="height:100%;text-align:center;overflow:auto;" id="logdataId"> 
				<table class="simple" style="width:100%;">
					<thead>
				       <tr>
				         <th width="15%">级别</th>
				         <th width="20%">设备</th>
				         <th width="20%">时间</th>
				         <th width="45%">日志内容</th>
				       </tr>
				    </thead>
				    <tbody id="deviceTableBody">
				    </tbody>
				</table>
			 </div>
		</div>
	</div>
	<img src="../../img/icon/nav_up.gif" style="display:none; position:absolute; right:33px; top:9px; cursor:pointer" title="隐藏查询条件面板" id="hideImg" />
	
	<!-- 开始控制声音播放 -->
<div class="jp-cont">
  <div id="jquery_jplayer_1" class="jp-jplayer"></div>
  <a href="#" id="unmute" class="volumn_btn unmute_on"></a> <a href="#" id="mute" class="volumn_btn mute_off"></a> </div>
</body>
</html>