webpackJsonp([36],{EXQ6:function(t,e){},xzKK:function(t,e,r){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=r("lC5x"),n=r.n(a),s=r("J0Oq"),i=r.n(s),o=r("D+g7"),u=r("/vFN"),c=r("Y3Ip"),l=r("gyMJ"),p=r("9ER+"),d=r("ki+6"),m={components:{PaymentTicket:o.a,PaymentWay:u.a,PaymentDetail:c.a,PaymentStatus:d.a},props:{orderNo:{type:[String,Number]}},computed:{title:function(){return(this.orderDetail.startCity||"")+" - "+(this.orderDetail.endCity||"")},subTitle:function(){return"出发时间 "+Object(p.d)(this.orderDetail.startTime)+" ("+Object(p.h)(this.orderDetail.startTime)+") "+Object(p.c)(this.orderDetail.startTime)},price:function(){return this.orderDetail.totalPrice}},data:function(){return{orderDetail:{},paymentStatus:0}},created:function(){},beforeRouteEnter:function(t,e,r){var a=this;return i()(n.a.mark(function e(){var s;return n.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=3,Object(l.z)({orderNo:t.query.orderNo});case 3:s=e.sent,r(function(t){return t.setData(s)});case 5:case"end":return e.stop()}},e,a)}))()},methods:{getOrderInfo:function(){var t=this;return i()(n.a.mark(function e(){var r,a;return n.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return r={orderNo:t.orderNo},e.next=3,Object(l.z)(r);case 3:a=e.sent,t.setData(a);case 5:case"end":return e.stop()}},e,t)}))()},setData:function(t){this.orderDetail=t,this.paymentStatus=t.status}}},f={render:function(){var t=this,e=t.$createElement,r=t._self._c||e;return r("main-page",{staticClass:"payment-container",attrs:{title:"订单",back:t.$store.getters.haveRouteFrom}},[r("template",{slot:"scroll"},[r("payment-ticket",{staticClass:"payment-pay-ticket",attrs:{title:t.title,subTitle:t.subTitle,price:t.price,tag:"单程",status:t.paymentStatus}},[r("payment-detail",{attrs:{detail:t.orderDetail}})],1),t._v(" "),r("payment-status",{attrs:{type:"plane",orderInfo:t.orderDetail,status:t.paymentStatus},on:{"update:status":function(e){t.paymentStatus=e}}})],1)],2)},staticRenderFns:[]};var y=r("C7Lr")(m,f,!1,function(t){r("EXQ6")},null,null);e.default=y.exports}});