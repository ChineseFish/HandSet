webpackJsonp([54],{"4o4c":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("4TJr"),o=a("iLhm"),s={components:{TicketCollector:i.a,TopayBar:o.a},computed:{detailData:function(){return this.$store.getters.detailData.homestay},collector:function(){return this.$store.getters.collector},duringDays:function(){return this.detailData.checkInDate&&this.detailData.checkOutDate?(+new Date(this.detailData.checkOutDate)-+new Date(this.detailData.checkInDate))/864e5:0},bookNoticeText:function(){return'\n        <p>根据我国法律规定，外国人、港澳台居民应当在抵达住宿地后24小时内办理住宿登记。住宿宾馆的，请在宾馆内办理。住宿宾馆以外地区的，请前往居住地派出所办理。未依法办理住宿登记将承担相应法律责任。</p>\n        <p>请随身携带您的出入境证件以备公安机关查验。</p>\n        <div style="margin-top: 15px;">\n          <p>According to the Chinese laws, foreign nationals and residents from Hong Kong, Macao and Taiwan should register their accommodation within 24 hours of arrival. The hotel residents can complete it in the hotel. Those who stay elsewhere should go to the local police station for registration. Failure to comply with the regulations will result in legal liability.</p>\n          <p>Please carry your travel document with you for possible examination by the police.</p>\n        </div>\n      '}},data:function(){return{showBookingNotice:!1}},created:function(){this.$store.commit("GET_COLLECTOR"),this.$store.commit("GET_DETAILDATA",{type:"homestay"})},mounted:function(){this.detailData.collector=this.collector},methods:{toShowBookingNotice:function(){this.showBookingNotice=!0}}},l={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("main-page",{staticClass:"homestay-booking-wrapper",attrs:{back:"",title:"预订信息"}},[a("template",{slot:"scroll"},[a("div",{staticClass:"block-section detail-wrapper"},[a("img",{staticClass:"thumb",attrs:{src:t.detailData.image}}),t._v(" "),a("div",{staticClass:"right-content"},[a("div",{staticClass:"title"},[t._v(t._s(t.detailData.name))]),t._v(" "),a("div",{staticClass:"sub-title"},[t._v(t._s(t.detailData.layout)+" | "+t._s(t.detailData.houseType))])])]),t._v(" "),a("div",{staticClass:"block-section"},[a("div",{staticClass:"title"},[t._v("入离日期")]),t._v(" "),a("div",{staticClass:"date-wrapper"},[a("div",[a("span",{staticClass:"date-num"},[t._v(t._s(t._f("dateFormat")(t.detailData.checkInDate)))]),t._v("入住")]),t._v(" "),a("div",{staticClass:"homestay-during-days"},[t._v(t._s(t.duringDays)+"晚")]),t._v(" "),a("div",[a("span",{staticClass:"date-num"},[t._v(t._s(t._f("dateFormat")(t.detailData.checkOutDate)))]),t._v("离店")])])]),t._v(" "),a("div",{staticClass:"block-section"},[a("div",{staticClass:"title"},[t._v("入住人信息")]),t._v(" "),a("ticket-collector",{attrs:{type:"homestay"},model:{value:t.collector,callback:function(e){t.collector=e},expression:"collector"}})],1),t._v(" "),a("div",{staticClass:"block-section"},[a("div",{staticClass:"title"},[t._v("发票")]),t._v(" "),a("div",[t._v("如需发票，可向房东索取")])]),t._v(" "),a("div",{directives:[{name:"is-link",rawName:"v-is-link"}],staticClass:"block-section"},[a("div",{staticStyle:{display:"flex","align-items":"center"},on:{click:t.toShowBookingNotice}},[a("span",[t._v("境外/港澳台人士入住须知")]),t._v(" "),a("i",{staticClass:"iconfont icon-wenhao",staticStyle:{"margin-left":"5px"}})])])]),t._v(" "),a("topay-bar",{attrs:{slot:"bottom",orderObj:t.detailData,type:"homestay"},slot:"bottom",model:{value:t.detailData.totalPrice,callback:function(e){t.$set(t.detailData,"totalPrice",e)},expression:"detailData.totalPrice"}}),t._v(" "),a("mu-dialog",{attrs:{open:t.showBookingNotice,scrollable:"",padding:80,title:"境外/港澳台入住须知"},on:{"update:open":function(e){t.showBookingNotice=e}}},[a("div",{domProps:{innerHTML:t._s(t.bookNoticeText)}})])],2)},staticRenderFns:[]};var c=a("C7Lr")(s,l,!1,function(t){a("KfDy")},null,null);e.default=c.exports},KfDy:function(t,e){}});