<template>
  <div>
    
    <!-- 电脑（已废弃） -->
    <div v-if="false">
      
      <div style="width: 80%;margin: 30px auto;display: flex;" class="myborder">
        <!--图 -->
        <div >
          <img  loading="lazy"  :src="require(`@/assets/${OneData.photo}.webp`)" class="myborder" style="height:320px;width: 320px;object-fit:cover;margin: 30px 100% 30px 30px;">
        </div>
        <!--信息 -->
        <div style="margin: 10px 0 10px 50px;text-align: left;">
          <h1>&lt;&lt;&nbsp;&nbsp;{{OneData.name}}&nbsp;&nbsp;>></h1>
          <h1 style="font-weight:bolder;color: red;"><span style="font-size: x-large;">¥</span>&nbsp;{{OneData.price}}</h1>
          <p>{{OneData.info}}</p>
          
          <!-- 评分 -->
          <div style="margin-top: 30px;">
            <span style="margin: 10px 0px 10px -2px;color: #00000060;">商品评分：</span>
            <van-rate v-model="mobile.rate_click_value" allow-half void-icon="star" void-color="#eee" @change="rateonChange" />
            <span style="font-size: small;margin: -100px 0px 0px 10px;color: #00000060;">({{mobile.rate_value?mobile.rate_value:0}}分,{{mobile.rate_num}}人评价)</span>
          </div>

          <!-- 同类商品标签 -->
          <div style="margin-top: 30px;display: flex;">
            <div style="cursor: pointer;margin-right: 10px;" v-for="(item, index) in type2_list" :key="index">
              <el-tag v-if="Object.values(item)[0]===OneData.name" >
                {{ Object.values(item)[0] }}
              </el-tag>
              <el-tag type="info" v-else @click="$router.push(`/product?id=`+Object.entries(item).map(entry => `${entry[0]}`)),mobile.query_id = Object.entries(item).map(entry => `${entry[0]}`)[0],test()">
                {{ Object.entries(item).map(entry => `${entry[1]}`)[0] }}
              </el-tag>
            </div>
          </div>

          <!-- 其他 -->
          <div style="margin: 30px 0 40px 0;display: flex;" class="mc2">
            <el-button size="medium" type="warning" style="color:white;width:200px;" @click="addtobuylist">加入购物车</el-button>
            <el-button size="medium"  style="color:white;background-color: red;width:200px;" @click="direct_buy">直接购买</el-button>
            <el-input-number style="top: 0px;font-size: larger;margin-left: 40px;" v-model="product.num" :min="1" :max="OneData.num" label="数量">
            </el-input-number>

            <div style="display: flex;color: #00000060;padding: 40px 0 0 20px;" class="mc2">
              <div>库存：{{OneData.num}}</div> <div style="margin-left: 20px;">浏览量：{{OneData.visited_num}}</div> <div style="margin-left: 20px;">销量：{{OneData.sold_num}}</div>
            </div>
            
          </div>
          <h4 style="">上架日期：{{ OneData.create_time.replace(/T/g, " ") }}</h4>
        </div>
      </div>

       <!-- 筛选排序 -->
       <van-dropdown-menu style="margin-left: 9%;position: relative;margin-bottom: 5px;" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
      </van-dropdown-menu>

      <!--评价 -->
      <div style="width:80%;text-align: left;margin: 10px auto;background-color: white;padding-left: 10px;" class="myborder">
        <h3>商品评论<span style="font-size: small;font-weight:inherit;color: rgba(0,0,0,0.6);">（{{ `共${$refs?.comment?.total}条` }}）</span></h3>
        <!--评价骨架 -->
        <van-skeleton title avatar :row="3" :loading="mobile.van_skeleton_loading" row-width="100%" 
        style="margin: 0 0 20px -10px;">
          <C  :value2="value2" ref="comment" style="margin: 15px 10px 0 ;" :product_id="mobile.query_id">实际内容</C>
        </van-skeleton>
      </div>

      <!-- 评论 -->


    </div>

    <!-- 手机 -->
    <div v-else>

      <!--信息 -->
      <div :style="`width:${product_width}%;text-align: center;margin: 25px auto 5px;padding-left: 10px;`" class="myborder">
        <!--题目 -->
        <h2 style="margin-top: 25px;margin-bottom: 5px;color:var(--ptext-color);">{{OneData.name}}&nbsp;&nbsp;</h2>
        
        <!--描述信息 -->
        <div class="color0000060" style="margin-top: 10px;">
          <i class="el-icon-view"></i>
          <span style="margin-left: 3px;">{{OneData.visited_num}}</span>

          <i style="margin-left: 10px;" class="el-icon-chat-round" ></i>
          <span style="margin-left: 3px;">{{$route.query.comment_num}}</span>

          <van-icon style="margin-left: 10px;" size="17" name="good-job-o" />
          <span style="margin-left: 3px;">{{OneData.love_list?OneData.love_list.length:0}}</span>
      
          <span style="margin-left: 15px;">{{OneData.create_time?.slice(0,10)}}</span>
        
        </div>

        <!--正文 -->
        <mavon-editor class="mdshow" v-model="OneData.content" :xssOptions="xssOptions" />

        <!--点赞-->
        <div class="dianzanblog" @click="send_product_zan()">
          <svg v-if="!Is_Zan" style="display:block;cursor: pointer;" t="1636093575017" class="icon hoverable2" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3323" width="70px" height="70px">
            <path d="M594.176 151.168a34.048 34.048 0 0 0-29.184 10.816c-11.264 13.184-15.872 24.064-21.504 40.064l-1.92 5.632c-5.632 16.128-12.8 36.864-27.648 63.232-25.408 44.928-50.304 74.432-86.208 97.024-23.04 14.528-43.648 26.368-65.024 32.576v419.648a4569.408 4569.408 0 0 0 339.072-4.672c38.72-2.048 72-21.12 88.96-52.032 21.504-39.36 47.168-95.744 63.552-163.008a782.72 782.72 0 0 0 22.528-163.008c0.448-16.832-13.44-32.256-35.328-32.256h-197.312a32 32 0 0 1-28.608-46.336l0.192-0.32 0.64-1.344 2.56-5.504c2.112-4.8 5.12-11.776 8.32-20.16 6.592-17.088 13.568-39.04 16.768-60.416 4.992-33.344 3.776-60.16-9.344-84.992-14.08-26.688-30.016-33.728-40.512-34.944zM691.84 341.12h149.568c52.736 0 100.864 40.192 99.328 98.048a845.888 845.888 0 0 1-24.32 176.384 742.336 742.336 0 0 1-69.632 178.56c-29.184 53.44-84.48 82.304-141.76 85.248-55.68 2.88-138.304 5.952-235.712 5.952-96 0-183.552-3.008-244.672-5.76-66.432-3.136-123.392-51.392-131.008-119.872a1380.672 1380.672 0 0 1-0.768-296.704c7.68-72.768 70.4-121.792 140.032-121.792h97.728c13.76 0 28.16-5.504 62.976-27.456 24.064-15.104 42.432-35.2 64.512-74.24 11.904-21.184 17.408-36.928 22.912-52.8l2.048-5.888c6.656-18.88 14.4-38.4 33.28-60.416a97.984 97.984 0 0 1 85.12-32.768c35.264 4.096 67.776 26.88 89.792 68.608 22.208 42.176 21.888 84.864 16 124.352a342.464 342.464 0 0 1-15.424 60.544z m-393.216 477.248V405.184H232.96c-40.448 0-72.448 27.712-76.352 64.512a1318.912 1318.912 0 0 0 0.64 282.88c3.904 34.752 32.96 61.248 70.4 62.976 20.8 0.96 44.8 1.92 71.04 2.816z" p-id="3324" fill="#9499a0">
            </path>
          </svg>
          <svg v-else style="display:block;cursor: pointer;" t="1636093991833" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4447" width="70px" height="70px">
            <path d="M860.032 341.12h-182.08c7.488-17.408 14.72-38.528 18.048-60.544 5.952-39.872 4.992-87.36-18.368-129.088-21.76-38.848-50.304-60.928-83.52-61.376-30.72-0.384-53.888 18.176-65.728 33.408a199.296 199.296 0 0 0-32.064 59.264l-1.92 5.184c-5.44 14.976-10.88 29.952-23.04 51.456-19.712 34.816-48.832 56.128-77.696 74.368a391.936 391.936 0 0 1-30.976 17.92v552.448a4621.952 4621.952 0 0 0 351.872-5.312c51.264-2.752 100.672-28.544 127.488-76.032 24.32-43.136 55.168-108.16 74.368-187.264 20.416-84.16 24.64-152.704 24.576-195.968-0.128-46.336-38.72-78.4-80.96-78.4z m-561.344 541.312V341.12H215.808c-59.712 0-113.408 42.048-120.896 104.32a1376 1376 0 0 0 0.64 330.368c7.36 58.688 56.128 100.032 113.024 102.848 25.024 1.28 55.552 2.56 90.112 3.712z" p-id="4448" fill="#00aeec">
            </path>
          </svg>
        </div>
        
        
        
        <div style="margin: 10px auto;" class="color0000060">
          <div >创建时间：{{OneData.create_time?.replace('T',' ') }}</div>
          <div >修改时间：{{OneData.update_time?.replace('T',' ') }}</div>
          <div >作者：{{product_user_name }}</div>
        </div>

      </div>


      <!-- 筛选排序 -->
      <van-dropdown-menu :style="`position: relative;margin-bottom: -3px;margin-left: ${$store.state.IsMobile?'-5px;':'3%;'}`" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
      </van-dropdown-menu>

      <!--评价 -->
      <div ref="commentdiv" :style="`width:${product_width}%;text-align: left;margin: 10px auto;padding-left: 10px;`" class="myborder">
        <h3 style="color:var(--ptext-color);">评论<span style="font-size: small;font-weight:inherit;color: rgba(0,0,0,0.6);color:var(--ptext-color)">（{{ `共${$refs.comment?$refs.comment.total:'..'}条` }}）</span></h3>
        <div style="margin: 10px 10px 10px 10px;color: #00000060;display: none;">暂无评论</div>
        
        <!--评价骨架 -->
        <van-skeleton title avatar :row="3" :loading="mobile.van_skeleton_loading" row-width="100%" 
        style="margin: 0 0 20px -10px;">
          <C  :value2="value2" ref="comment" style="margin: -5px 0 0 0px;" :product_id="mobile.query_id">实际内容</C>
        </van-skeleton>
      </div>

      <!--防被下栏挡 -->
      <div style="height: 60px;width: 100%;"></div>








    </div>

    <!--评论 -->
    <div class="demo-nav3" :style="`transition: margin-bottom 0.5s ease;margin-bottom: ${comment_margin_bottom}px;`">
        
        <!-- 评论框 -->
        <div ref="inputBox" style="height: 200px;background-color: rgb(243, 243, 244);overflow-x: hidden;" >
          <div class="el-input" style="position: relative;display: flex;justify-content: start;">
          <input
              style="width: 85%;margin: 5px auto;"
              class="el-input__inner"
              v-model="comment_info"
              @keyup.enter="sendComment"
              enterkeyhint="send"
              placeholder="发布一条评论吧"
              type="text"
              
            />
            <div style="display: flex;justify-content: center;align-items: center;">
              <van-icon @click="comment_margin_bottom==='0'?comment_margin_bottom='150':comment_margin_bottom='0'" name="smile-o" size="30" style="margin-right: 8px;cursor: pointer;"/>
            </div>
          </div>
          <div style="height: 150px;overflow-y: scroll;">
            <EmojiPicker @emoji-selected="selectEmoji" />
          </div>
        </div>

        <!-- 评论框遮罩（未登录不能评论） -->
        <div @click="$router.push(`/login`)" v-if="!this.$store.state.UserId" :style="`transition: margin-bottom 0.5s ease;margin-bottom: ${comment_margin_bottom}px;background-color: rgba(240,243,244,0.7);position: fixed;bottom:0;width: 100%;height: 50px;z-index: 99;`">
          <p style="display: flex;justify-content: center;align-items: center;color: gray;">点击登录后评论</p> 
        </div>

    </div>

    <draggable class="drag-tool" :X=$store.state.CURRENT_WIDTH-200 :Y=200>
        <div style="width: 100px;margin: 20px auto;" @mousedown.stop >
            <!-- 滑动选择宽度 -->
            <el-slider style="margin: 0;padding: 0;"
            title="调节阅读页宽"
            :min=50  
            v-model="product_width"></el-slider>
        </div>
    </draggable>


  </div>
</template>

<script>
import axios from '@/utils'

import { Dialog } from 'vant';
import { Toast } from 'vant';
import { Rate } from 'vant';
import { Skeleton } from 'vant';
import { DropdownMenu, DropdownItem } from 'vant';
import C from '@/components/comment/My_Test.vue';

import { throttle } from 'lodash';
import EmojiPicker from '@/components/emoji/EmojiPicker.vue';
import { Icon } from 'vant';
import draggable from '@/components/draggable/VE.vue'

export default {
    components: {
        draggable,
        'van-rate': Rate,
        'van-skeleton':Skeleton,
        'van-dropdown-menu':DropdownMenu,
        'van-dropdown-item':DropdownItem,

        'van-icon':Icon,
        EmojiPicker,
        C
    },
    data() {
        return{
            product_width:80,
            xssOptions: {
                whiteList: {
                    a: ["href", "title", "target", "download"],
                    img: ["src", "alt", "width", "height"],
                    video: ["src", "type", "controls", "width", "height", "poster"],
                    source: ["src", "type"],
                    br: [],
                    div: ["class","style"],
                    iframe: ["style","src", "scrolling", "border", "frameborder", "framespacing", "allowfullscreen","width", "height"]
                },
                stripIgnoreTagBody: true
            },
            product_user_name:"未知",
            Is_Zan:false, // 你点赞没啊
            //
            editorOptions: {
                readOnly: true, // 设置为只读，禁止编辑
                hideToolbar: true, // 隐藏工具栏
                hideBorder: true, // 隐藏边框
            },
            //
            comment_margin_bottom:'-50',
            van_goods_margin_bottom:"0",
            //
            comment_info:"",
            value2: 'a',
            option2: [
                { text: '点赞排序', value: 'a' },
                { text: '时间排序', value: 'b' },
            ],
            dropdown_isclick:false,
            //
            type2_list:[],
            OneData:{
                photo:'load',
                create_time:1,
                love_list:[]
            },
            IsTableLoading:true,
            product:{
                num:0
            },
            mobile:{
                van_skeleton_loading:false,
                query_id:null,
                TotalBuylistNum:0,
                show:false,
                activeIndex:1,
                unselected_style:"color: black;border: 1px solid white;background-color:ghostwhite;border-radius: 15px;display: inline-block;margin-left: 5%;",
                selected_style:"color: red;border: 1px solid red;background-color:ghostwhite;border-radius: 15px;display: inline-block;margin-left: 5%;",
                direct_buy:false,
                Datas:[],
                rate_click_value:null,
                rate_value:null,
                rate_num:null
            }
        }
    },
    methods:{
        is_userId_in_loveList(){
            axios.get('/user/name')
                .then(response=>{
                    if(response.data.code){
                        response.data.map.user_id
                        if(this.OneData.love_list.indexOf(response.data.map.user_id)!==-1){
                            this.Is_Zan = true
                            console.log("is_userId_in_loveList():存在")
                        }else console.log("is_userId_in_loveList():不存在")
                    }
                })
                .catch(error=>{
                    this.$message.error(error.data.msg);
                    console.log(error)
                    this.$store.state.IsLogin = false
                })

        },
        send_product_zan(){
            axios.post('/product/addone/zan',{"product_id":this.$route.query.id,})
                .then(response=>{
                    if(response.data.code===0)Toast.fail(response.data.msg)
                    else {
                        Toast.success(response.data.data);  
                        // ..这里处理本地数据
                        this.Is_Zan = !this.Is_Zan
                    }
                })
        },
        selectEmoji(emoji){
            console.log(emoji)
            this.comment_info += emoji
        },
        sendComment(){
            console.log("sendComment!!")
            // val是KeyEvent
            //this.$message(this.comment_info)
            axios.post('/comment/addone',{
                "comment_info":this.comment_info,
                "product_id":this.mobile.query_id,
                "father_comment_id":0,
                "replay_to_user_id":0 
            }).then(response=>{
                if(response.data.code===0)Toast.fail(response.data.msg)
                else {
                    Toast.success(response.data.data);       
                }
            })
            this.comment_info = ""
        },
        // 筛选框关闭触发
        dropdown_closed(value2){
            console.log(value2)
            if(this.dropdown_isclick){
                this.$refs.comment.dropdown_closed()
                this.dropdown_isclick = false
            }
        },
        // 跳url
        gotourl(val){
            window.location.href = val
        },
        // 手机-商品评分
        rateonChange(value) {
            if(!this.$store.state.IsLogin)
                return Toast.fail("请登录后评分")

            Toast('当前值：' + value)

            // 手机
            Toast.loading({
                message: '加载中...',
                forbidClick: true,
                duration: 0, // 持续展示 toast，直到被明确关闭
            });


            // 给商品评分
            axios.post('product-rate/add-or-update',{
                product_id:this.$route.query.id,
                rate:this.mobile.rate_click_value
            }).then(response=>{
                if(response.data.code===0){
                    Toast.clear();
                    Toast.fail(response.data.msg)
                }
                else {
                    Toast.clear();
                    Toast.success(response.data.data)
                    //this.$router.push('/user/login')
                }
                console.log(response)
            }).catch(error=>{
                Toast.clear();
                Toast.fail(error.data.msg);
                console.log(error)
            })

        },
        // 手机端点击下栏
        onClickIcon(val){
            console.log(val)
            // 1 客服 2,3 购物车 4 店铺 5 加购 6 立即购买
            if(val === 1)
                this.$router.push('/kefu')
            else if(val === 2 || val === 3){
                this.$router.push('/user/buylist')
            }
            else if(val === 4){
                this.$router.push('/home')
            }
            else if(val === 5){
                this.mobile.show = true
                this.mobile.direct_buy = false
            }
            else if(val === 6){
                this.mobile.show = true
                this.mobile.direct_buy = true
            }
            else Dialog.alert({message: "<h3>comming soon</h3>"})
        },
        // 初始化加载
        async test(){
            console.log(this.$route.query.id)
            console.log("mobile_show:"+this.$route.query.mobile_show)
            if(this.mobile.query_id===null)this.mobile.query_id = this.$route.query.id // 只在首次加载初始化
            if(this.$route.query.mobile_show)this.mobile.show = true
            await axios.get(`product/getone?id=${this.mobile.query_id?this.mobile.query_id:this.$route.query.id}`).then(response=>{
                if(response.data.data===null)this.$router.push('/404?msg=文章未找到或者被隐藏啦')
                this.OneData = response.data.data
                this.$nextTick(() => { // dom之后加载
                    this.is_userId_in_loveList()
                });
                this.IsTableLoading = false
                this.product_user_name = response.data.map.user_name
                this.mobile.rate_click_value = response.data.map.rate_value
                this.mobile.rate_num = response.data.map.rate_num
                console.log(response)
                // 拿同类商品
                //this.getalltype2()
                // 去除遮罩
                this.$store.state.zhezhao_show = false

            }).catch(error=>{
                console.log(error)
                //this.$router.push('/home')
            })
      
            // 加载新评论 (防止组件没加载完 延时)
            //setTimeout(() => { this.$refs.comment.dropdown_closed(this.mobile.query_id)},200)



      

        },
        addtobuylist(){
            if(!this.$store.state.IsMobile)
            // 电脑
                axios.post('buylist/add',{
                    product_num:this.product.num,
                    product_id:this.mobile.query_id
                }).then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        this.$message.success(response.data.data)
                        //this.$router.push('/user/login')
                    }
                    console.log(response)
                }).catch(error=>{
                    this.$message.error(error.data.msg);
                    console.log(error)
                })
            else{
                // 手机
                Toast.loading({
                    message: '加载中...',
                    forbidClick: true,
                    duration: 0, // 持续展示 toast，直到被明确关闭
                });

                if(this.mobile.direct_buy){
                    // 直接购买
                    this.direct_buy()
                }else{
                    // 加购物车
                    axios.post('buylist/add',{
                        product_num:this.product.num,
                        product_id:this.mobile.query_id
                    }).then(response=>{
                        if(response.data.code===0){
                            Toast.clear();
                            Toast.fail(response.data.msg)
                        }
                        else {
                            Toast.clear();
                            Toast.success(response.data.data)
                            this.getbuylist()
                            this.mobile.show = false
                            //this.$router.push('/user/login')
                        }
                        console.log(response)
                    }).catch(error=>{
                        Toast.clear();
                        Toast.fail(error.data.msg);
                        console.log(error)
                    })
                }
            }
        

        },
        // 手机-立刻购买
        direct_buy(){
            if(this.OneData.num === 0){
                Toast.fail('库存不足')
                return
            }
            console.log("直接购买，不加购")
            var buylist = {
                "id": null,
                "user_id": null,
                "product_id": this.$route.query.id,
                "create_time": null,
                "product_num": this.product.num,
                "is_selected": false
            }
            var product = this.OneData
            this.mobile.Datas = [
                {
                    buylist,
                    product
                }
            ]
            console.log(this.mobile.Datas)
            Toast.clear();
            this.$router.push({name:"userbuylist_result",params:{datas:this.mobile.Datas}})
        },
        // 拿购物车（这里只是为了获取购物车数量）
        getbuylist(){
            axios.get('/buylist/page',{
                params: {
                    currentPage: 1,
                    PageSize: 10
                }
            }).then(response=>{
                if(response.data.code===0)console.log()//this.$message.error(response.data.msg)
                else {
                    this.mobile.TotalBuylistNum = response.data.data.total
                    //this.$message.success("获取成功")
                }
            }).catch(error=>{
                //this.$message.error(error.data.msg)
                console.log(error)
            })
        },
        // 拿同类商品
        getalltype2(){
            console.log(this.OneData)
            axios.get('/product/getalltye2',{
                params: {
                    type2:this.OneData.type2
                }
            }).then(response=>{
                if(response.data.code===0)this.$message.error(response.data.msg)
                else {
                    this.type2_list = response.data.map.type2_list        
                }
            }).catch(error=>{
                this.$message.error(error.data.msg)
                console.log(error)
            })
        },
        // 监听滚动
        handleScroll(){
            const rect = this.$refs.commentdiv.getBoundingClientRect();
            //console.log("元素距离视口顶部的距离:", rect.top);
            if(this.$store.state.CURRENT_HEIGHT>(rect.top+200)){
                console.log("进入评论框")
                this.comment_margin_bottom = "0"
                this.van_goods_margin_bottom = "-50"
            }else{
                console.log("评论框之外")
                this.van_goods_margin_bottom = "0"
                this.comment_margin_bottom = "-50"
            }

        
        },
        // 监听点击
        handleOutsideClick(e) {
            // 检查点击事件是否在输入框（及其子元素）之外
            if (!this.$refs.inputBox.contains(e.target)) {
                this.comment_margin_bottom = "0"
            }
        }
    },
  

    mounted(){
        this.$store.state.zhezhao_show = false
        this.$store.state.zhezhao_show = true
        this.$store.state.PAGE_STATE = ""
        this.throttlehandleScroll = throttle(this.handleScroll,300) // 节流
        window.addEventListener('scroll', this.throttlehandleScroll);
        document.addEventListener('click', this.handleOutsideClick);
        this.test()
    },
    beforeDestroy(){
        this.$store.state.PAGE_STATE = "Tabbar"
        window.removeEventListener('scroll', this.handleScroll);
        document.removeEventListener('click', this.handleOutsideClick);
    }
}
</script>


<style >
.color0000060 > i{
    color:gray;
    margin-right: 4px;
}
.color0000060{
    color:var(--ptext-color2);
}
.el-input{
    background-color: var(--el-input--bg) !important;
}
.hljs {
  background-color: rgb(250,250,250) !important;
}
/* 代码字体大小 */
.markdown-body .highlight pre, .markdown-body pre {
    padding: 16px;
    overflow: auto;
    font-size: 1.1rem !important;
    line-height: 1.45;
    background-color: rgb(250,250,250) !important;
    border-radius: 3px;
}
.v-show-content{
  background-color: var(--product-bg) !important;
  transition:background-color 0.5s ease;
}
.v-note-wrapper{
  box-shadow: none !important;
  position: unset !important;
}

.mdshow{
    margin: 20px 0 0 0;
}

.mdshow > .v-note-op{
  display: none !important;
}
.v-note-edit{
  display: none !important;
}
.v-note-show{
  flex:0 0 100% !important;
  border:none !important;
  color:var(--ptext-color) !important;
}
input[type="text"]{
      font-size: 16px; /* 调整为至少 16px 不会触发移动端缩放 */
  }
.demo-nav2 {
    position: fixed;  /* 从 relative 改为 fixed */
    bottom: 0;           /* 定位到页面顶部 */
    left: 0;          /* 定位到页面左边 */
    width: 100%;      /* 让导航栏宽度扩展至全屏 */
    min-width: 1600px;
    display: flex;
    background-color: rgb(243,243,244);
    height: 50px;
}
.demo-nav3 {
    position: fixed;  /* 从 relative 改为 fixed */
    bottom: 0;           /* 定位到页面顶部 */
    left: 0;          /* 定位到页面左边 */
    width: 100%;      /* 让导航栏宽度扩展至全屏 */
    display: flex;
    background-color: rgb(243,243,244);
    height: 50px;
}

.el-table tbody tr:hover>td {
            background-color:unset !important 
        }

.red-border {
  border: 1px solid red;
  color: red;
}

  .myborder {
  border-radius: 5px;
  border: 5px solid var(--product-bg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  background-color: var(--product-bg);
  transition:background-color 0.5s ease, border 0.5s ease ;

}
.el-button--medium {
    padding: 20px 40px;
    font-size: 24px;
    border-radius: 4px;
}
.el-button--warning {
    color: #FFF;
    background-color:darkorange;
    border-color: darkorange;
}
.el-button--warning:hover {
    color: #FFF;
    background-color:rgba(255, 140, 0, 0.647);
    border-color: rgba(255, 140, 0, 0.647);
}
</style>

<style>
.dianzanblog{
  margin: 10px auto;
  height: 70px;
  width: 70px;
  border-radius: 20px;
  background-color: antiquewhite;
  position: relative;
}

.dianzanblog:hover{
  background-color: rgb(252, 235, 211);
}



.ql-toolbar{
  display: none;
}
.ql-container {
    border: none !important;
}


.el-input-number__increase, .el-input-number__decrease, .el-input-number .el-input__inner {
  /* 覆盖Element UI默认的聚焦边框颜色 */
  border-color: transparent !important;
}

.el-input-number__increase:focus, .el-input-number__decrease:focus, .el-input-number .el-input__inner:focus {
  /* 确保在聚焦状态下边框颜色被移除 */
  border-color: transparent !important;
  /* 移除外边框阴影 */
  box-shadow: none !important;
  /* 如果还有其他聚焦效果，也在这里移除 */
  outline: none !important;
}

.mc2 .el-input__inner{
  height: 65px;
  padding: 0;
}
.mc2 .el-input-number__decrease,.mc2 .el-input-number__increase{
  width: 40px;
  height: 63px;
  position: absolute;
  top:1px;
}

.mc2 .el-input-number {
  line-height: 65px;
  width:130px;
}
.drag-tool{
    width: 150px;
    height: 90px;
    background-color: var(--fifth-color);
    border-radius: 30px;
}
.el-slider{
    margin: 5px auto 0  !important;
}
</style>