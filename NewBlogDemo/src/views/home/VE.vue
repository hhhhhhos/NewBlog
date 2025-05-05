<template>
  <div style="margin-top: 0;padding-top: 0;margin-bottom: 50px;">
    

    <!-- 电脑 -->
    <div v-if="!this.$store.state.IsMobile" class="desktop">
      <!-- 图 -->
      <div 
        @click="$openURL2(selectedImage)"
      @mouseenter="onMouseEnter"
      @mousemove="handleMouseMove"
      @mouseleave="onMouseLeave"
      :style="`cursor: pointer;background-color:gray;background-image: url(${selectedImage});background-position:${this.backgroundPositionX}vw ${selectedImageBGposition};${donghua}`"
       class="bgphoto mybordertt">
        <div class="xiba">
          {{FType?`分类 の ${dataResult?.fenlei_map[FType]?dataResult.fenlei_map[FType]:FType==='0'?'无':'未知分类'}`:FType===0?'分类 の 无':`西巴の博客`}}
        <div class="xiba-sub">
            共{{ TotalPage }}篇
            <div class="xiba-sub-sub">
                {{FName?FName:''}}
                <div class="xiba-sub-sub">
                    {{tag_int_local?`# ${dataResult?.biaoqian_map[tag_int_local]}`:""}}
                </div>
            </div>
        </div>
        </div>

        <div class="xiba-version">
            {{`version  `}}<span class="xiba-version-num">240925</span>
        </div>

        <jiantou title="切换壁纸" class="jiantou"   
        @click="donghua='transition: background-image 1s ease;',getRandomImage(null,images)"
        />

        
      </div>

      <!-- 筛选排序 -->
      <van-dropdown-menu style="position: relative;margin-top:10px;color: var(--shaixuan-color);" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
        <!-- 访问量 -->
        <div style="margin: 0 10px 4px 0;position:absolute;color: var(--shaixuan-color);font-size: 16px;right:0;bottom:0;">
          <i class="el-icon-view" style="margin: 0 4px 0px 0;"></i>{{mobile.home_visitors}}
        </div>
      </van-dropdown-menu>

      <div v-loading=this.IsTableLoading style="min-height: 100px;">
        
        <!-- 是否展示全部文章 -->
        <el-button title="展示除了主页推荐外的所有文章" v-if="!IsTableLoading && !FType && !tag_int_local && !showAll" size="mini" round style="margin: 0;" @click="showAll=1,getproduct()">显示全部文章</el-button>

        <!-- 无限滚动 -->
        <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        offset=-40
        style="margin: 0px auto 20px ;"
        class="myvan"
        >
            
            <div 
            v-for="(product, index) in tableData" @click="cardclick(product.id,product.comment_num)"
            :key="index"
            style="cursor: pointer;position: relative;"
            class="mybordert"
            >

                <svg v-if="product.is_top" style="position: absolute;right: 0;margin: -5px -5px 0 0;" t="1722121138299" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4266" width="24" height="24"><path d="M951.296 424.96L1024 352.256 671.744 0 599.04 72.704l70.144 70.656-168.96 168.96a296.96 296.96 0 0 0-286.72 75.264L143.36 458.24 72.704 387.584 0 460.8l245.248 245.248-139.776 139.776 72.704 72.704 140.288-140.288L563.2 1024l72.704-72.704-70.144-70.656 70.144-70.144a296.96 296.96 0 0 0 75.776-287.232l168.96-168.96z" fill="#333333" p-id="4267"></path></svg>
        
                <div class="card-type" 
                @click.stop="FType = product.type , getproduct()"
                :style="`background-color:${dataResult.fenlei_color_map[product.type]?dataResult.fenlei_color_map[product.type]:'#555555'} ;`">
                    <div>
                        {{dataResult.fenlei_map[product.type]}}
                    </div>

                    <div 
                    v-if="false"
                    :style="`border-color:${darkenColor(dataResult.fenlei_color_map[product.type]?dataResult.fenlei_color_map[product.type]:'#555555', 20)} ${darkenColor(dataResult.fenlei_color_map[product.type]?dataResult.fenlei_color_map[product.type]:'#555555', 20)}  transparent transparent;`" 
                    class="card-type-triangle" />
                </div>

                <div 
                    :style="`border-color:${darkenColor(dataResult.fenlei_color_map[product.type]?dataResult.fenlei_color_map[product.type]:'#555555', 20)} ${darkenColor(dataResult.fenlei_color_map[product.type]?dataResult.fenlei_color_map[product.type]:'#555555', 20)}  transparent transparent;`" 
                    class="card-typetriangle" />
            
                <div class="card-out"
                @click="cardclick(product.id,product.comment_num)"
                :style="product.photo_url?
                    `background-image: url(`+product.photo_url+`);`
                    :product.photo!=='noproduct'?
                    'background-image: url( /xiba-newblog/img/'+`${product.id+'/'+product.photo}.webp`+');':`background-image: none;`">

                    <div class="card">
                        
                        <div class="card-title">
                        <h3 style="margin: 5px 0 0 5px;">{{product.name}}</h3>
                        </div>

                        <div class="card-info">
                        <p style="margin: 5px 0 0 5px;">{{product.info}}</p>
                        </div>


                    </div>

                </div>


                <div class="card-right">

                    <div class="card-tag">
                            <p class="card-tag-item" v-for="(tag_int,key) in tag_map?.[product.id]" :key="key"
                            @click.stop="tag_int_local = tag_int">
                                # {{ dataResult?.biaoqian_map[tag_int] }}
                            </p>
                    </div>

                    <div class="card-right-out">
                        <div class="card-right-out-in" v-html="extractText(product.content)"></div>
                    </div>

                    <div class="card-right-down">

                        <div class="card-detail2">
                            <i style="" class="el-icon-view"></i>
                            <span style="margin-left: 3px;">{{product.visited_num}}</span>

                            <i style="margin-left: 10px;" class="el-icon-chat-round" ></i>
                            <span style="margin-left: 3px;">{{product.comment_num}}</span>

                            <van-icon style="margin-left: 10px;" size="17" name="good-job-o" />
                            <span style="margin-left: 3px;">{{product.love_list?product.love_list.length:0}}</span>
                        </div>

                        <div class="card-create-time2">
                            {{product.create_time?.slice(0, 10)}}
                        </div>

                    </div>

                </div>

            </div>
    
        </van-list>

        <!-- 是否展示全部文章 -->
        <el-button title="展示除了主页推荐外的所有文章" v-if="!IsTableLoading && !FType && !tag_int_local && !showAll" size="mini" round style="margin: 0 0 5px 0;" @click="showAll=1,getproduct()">显示全部文章</el-button>

      </div>

    </div>

    
    <!-- 手机 -->
    <div v-else style="margin-top: 50px;">

      <div style="display: flex;align-items: center;justify-content:inherit;background-color:#ffffff;"  >
        <Search
          style="width: 100%;"
          v-model=input
          shape="round"
          background="#ffffff"
          placeholder="请输入搜索关键词"
          @search="clicksearch"
        />
      </div>

    
      <Tabs v-model="activeIndex2" style="margin-top: 0;" animated swipeable>
        <Tab>
            <template #title>
                    <van-dropdown-menu>
                        <van-dropdown-item @change="dropdown_item_change" v-model="value1m" :options="option1m" get-container="body" />
                    </van-dropdown-menu>
                    <div v-if="value1m!==0" style="position: absolute;bottom:0;left: 50%;transform: translateX(-50%);background-color:deeppink;width: 50%;height: 2px;" />
            </template>
        </Tab>
        <Tab>
            <template #title>
                <van-dropdown-menu>
                    <van-dropdown-item @change="dropdown_item_change2" v-model="value2m" :options="option2m" get-container="body" />
                </van-dropdown-menu>
                <div v-if="value2m!==0" style="position: absolute;bottom:0;left: 50%;transform: translateX(-50%);background-color:green;width: 50%;height: 2px;" />
            </template>
        </Tab>
      </Tabs>

      <!-- 筛选排序 -->
      <van-dropdown-menu style="position: relative;" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
        <!-- 访问量 -->
        <div style="margin: 0 10px 4px 0;position:absolute;color: #00000060;font-size: small;right:0;bottom:0;">
          <i class="el-icon-view"></i>{{mobile.home_visitors}}
        </div>
        
        <!-- 是否展示全部文章 -->
        <el-button title="展示除了主页推荐外的所有文章" v-if="!IsTableLoading && !FType && !tag_int_local && !showAll" size="mini" round style="margin: 0;position: absolute;top:0;left:50%;transform: translateX(-50%)" @click="showAll=1,getproduct()">显示全部文章</el-button>

      </van-dropdown-menu>

      

      <div v-loading=this.IsTableLoading style="min-height: 100px;">
        
        <!-- 无限滚动 -->
        <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        offset=0
        >

          <div style="margin: 5px 5px 10px 5px;position: relative;" v-for="(product, index) in tableData" @click="cardclick(product.id,product.comment_num)"
                :key="index">
                <svg v-if="product.is_top" style="z-index:2;position: absolute;right: 0;margin: -5px -5px 0 0;" t="1722121138299" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4266" width="16" height="16"><path d="M951.296 424.96L1024 352.256 671.744 0 599.04 72.704l70.144 70.656-168.96 168.96a296.96 296.96 0 0 0-286.72 75.264L143.36 458.24 72.704 387.584 0 460.8l245.248 245.248-139.776 139.776 72.704 72.704 140.288-140.288L563.2 1024l72.704-72.704-70.144-70.656 70.144-70.144a296.96 296.96 0 0 0 75.776-287.232l168.96-168.96z" fill="#333333" p-id="4267"></path></svg>
            <Card
              style="border-radius: 10px; overflow: hidden;background-color: white;text-align: left;"
              :thumb="product.photo_url?product.photo_url
                :'/xiba-newblog/img/'+`${product.id+'/'+product.photo}.webp`"
            >
            <template #tag>
                <div class="card-type-mini"   :style="`background-color:${dataResult.fenlei_color_map[product.type]?dataResult.fenlei_color_map[product.type]:'#555555'} ;`">
                    <div>{{dataResult.fenlei_map[product.type]}}</div>
                </div>
            </template>
            <template #tags>
                <div style="margin: 10px 5px;">
                    <van-tag v-for="(tag_int,key) in tag_map?.[product.id]" :key="key"
                    style="margin-right: 4px;cursor: pointer;"  mark color="#1E90FF" @click="tag_int_local=tag_int,value2m=tag_int">
                        {{ dataResult?.biaoqian_map[tag_int] }}
                    </van-tag>
                </div>
            </template>
            <template #title >
              <h3 style="margin: 5px 0 0 5px;">{{product.name}}</h3>
            </template>

            <template #desc >
              <p style="margin: 5px 0 0 5px;">{{product.info}}</p>
            </template>

            <template #bottom >
              <div style="float:left;margin-left: 5px;">
                <i style="color: gray;" class="el-icon-view"></i>
                <span style="margin-left: 3px;color: #00000060;">{{product.visited_num}}</span>

                <i style="margin-left: 10px;color: gray;" class="el-icon-chat-round" ></i>
                <span style="margin-left: 3px;color: #00000060;">{{product.comment_num}}</span>

                <svg  style="color: #00000060;display:inline;margin:0px 0px -3px 10px;cursor: pointer;" t="1636093575017" class="icon hoverable2" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3323" width="14px" height="14px">
                  <path d="M594.176 151.168a34.048 34.048 0 0 0-29.184 10.816c-11.264 13.184-15.872 24.064-21.504 40.064l-1.92 5.632c-5.632 16.128-12.8 36.864-27.648 63.232-25.408 44.928-50.304 74.432-86.208 97.024-23.04 14.528-43.648 26.368-65.024 32.576v419.648a4569.408 4569.408 0 0 0 339.072-4.672c38.72-2.048 72-21.12 88.96-52.032 21.504-39.36 47.168-95.744 63.552-163.008a782.72 782.72 0 0 0 22.528-163.008c0.448-16.832-13.44-32.256-35.328-32.256h-197.312a32 32 0 0 1-28.608-46.336l0.192-0.32 0.64-1.344 2.56-5.504c2.112-4.8 5.12-11.776 8.32-20.16 6.592-17.088 13.568-39.04 16.768-60.416 4.992-33.344 3.776-60.16-9.344-84.992-14.08-26.688-30.016-33.728-40.512-34.944zM691.84 341.12h149.568c52.736 0 100.864 40.192 99.328 98.048a845.888 845.888 0 0 1-24.32 176.384 742.336 742.336 0 0 1-69.632 178.56c-29.184 53.44-84.48 82.304-141.76 85.248-55.68 2.88-138.304 5.952-235.712 5.952-96 0-183.552-3.008-244.672-5.76-66.432-3.136-123.392-51.392-131.008-119.872a1380.672 1380.672 0 0 1-0.768-296.704c7.68-72.768 70.4-121.792 140.032-121.792h97.728c13.76 0 28.16-5.504 62.976-27.456 24.064-15.104 42.432-35.2 64.512-74.24 11.904-21.184 17.408-36.928 22.912-52.8l2.048-5.888c6.656-18.88 14.4-38.4 33.28-60.416a97.984 97.984 0 0 1 85.12-32.768c35.264 4.096 67.776 26.88 89.792 68.608 22.208 42.176 21.888 84.864 16 124.352a342.464 342.464 0 0 1-15.424 60.544z m-393.216 477.248V405.184H232.96c-40.448 0-72.448 27.712-76.352 64.512a1318.912 1318.912 0 0 0 0.64 282.88c3.904 34.752 32.96 61.248 70.4 62.976 20.8 0.96 44.8 1.92 71.04 2.816z" p-id="3324" fill="#9499a0">
                  </path>
                </svg>
                <span style="margin-left: 3px;color: #00000060;">{{product.love_list?product.love_list.length:0}}</span>
              </div>
              
              <span style="margin-left: 10px;color: #00000060;float: right;">{{product.create_time?.slice(0,10)}}</span>
            
            </template>
            </Card>
          </div>
    
        </van-list>

        <!-- 是否展示全部文章 -->
        <el-button title="展示除了主页推荐外的所有文章" v-if="!IsTableLoading && !FType && !tag_int_local && !showAll" size="mini" round style="margin: 0;" @click="showAll=1,getproduct()">显示全部文章</el-button>


      </div>
    
    </div>

    <footer style="text-align:center;color: #bdc2c6;margin:0 0 20px 0;font-size: small;cursor: pointer">
        <a href="https://beian.miit.gov.cn/" target="_blank" style="text-decoration:none;color:#bdc2c6;">粤ICP备2023124224号</a>
    </footer>


    <!--接收product_page_FType的总线事件-->
    <rootEvent event_name="product_page_FType" @rootEvent="product_page_FType_Event" />

    <!--接收clicksearch的总线事件-->
    <rootEvent event_name="clicksearch" @rootEvent="clicksearch" />

  </div>
</template>

<script>
import axios from '@/utils'
import { Search,Card,Tabs,Tab } from 'vant';
import { List} from 'vant';
import { DropdownMenu, DropdownItem } from 'vant';
import rootEvent from '@/components/receivedrootevent/VE.vue'
import { throttle } from 'lodash';
import { Icon } from 'vant';
import jiantou from '@/components/jiantou/VE.vue'
import { Tag } from 'vant';
//const failedIndices = new Set();

export default {
    components:{
        'van-tag':Tag,
        jiantou,
        'van-icon':Icon,
        rootEvent,
        Search,
        Card,
        Tabs,
        Tab,
        'van-list':List,
        'van-dropdown-menu':DropdownMenu,
        'van-dropdown-item':DropdownItem,
    },
    data() {
        return{
            pages:2,
            showAll:0,
            // 手机选分类标签
            value1m:0,
            value2m: 0,
            option1m: [
                { text: '分类', value: 0 },
                { text: '全部商品', value: 1 },
                { text: '新款商品', value: 2 },
                { text: '活动商品', value: 3 },
            ],
            option2m: [
                { text: '标签', value: 0 },
                { text: '默认排序', value: 1 },
                { text: '好评排序', value: 2 },
                { text: '销量排序', value: 3 },
            ],
            //
            tag_int_local:null,
            tag_map:[],
            donghua:'transition: background-image 1s ease;',
            isenter2:false,
            Xmove:-2.5, // 偏移量 多出长度的一半
            initialX: 0,
            backgroundPositionX: -2.5, // 初始位置在中间（要和偏移量一样）
            dataResult:{
                home_visitors:null,
                back_visitors:null,
                comment_total_num:null,
                product_total_num:null,
                en_list:null,
                cn_list:null,
                co_list:null,
                po_list:null,
                fenlei_map:null,
                biaoqian_map:null
            },
            // 首页图list
            images:[
                "https://cdn-l-cyberpunk.cdprojektred.com/wallpapers/3840x2160/CNY_Artwork-zh-cn.jpg",
                "https://w.wallhaven.cc/full/6d/wallhaven-6d6yy6.jpg",
                "https://w.wallhaven.cc/full/gp/wallhaven-gpgddl.jpg",
                "https://w.wallhaven.cc/full/1p/wallhaven-1py3r1.jpg",
                "https://w.wallhaven.cc/full/qz/wallhaven-qzq5l7.jpg",
                //"https://w.wallhaven.cc/full/m3/wallhaven-m3eve8.jpg",
                "https://w.wallhaven.cc/full/p9/wallhaven-p9pro3.jpg",
                "https://w.wallhaven.cc/full/gp/wallhaven-gpg5ql.jpg",
                "https://w.wallhaven.cc/full/2y/wallhaven-2yxp16.jpg",
                "https://w.wallhaven.cc/full/we/wallhaven-wekp5x.jpg",
                "https://w.wallhaven.cc/full/d6/wallhaven-d6z96o.jpg",
            ],
            selectedImage:"",
            selectedImageBGposition:"",
            // region vant无限滚动手机参数
            loading: false,
            finished: false,
            // end region
            // region vant主页筛选商品/排序
            value2: 'b',
            option2: [
                { text: '时间升序', value: 'a' },
                { text: '时间降序', value: 'b' },
                { text: '访问量排序', value: 'c' },
                //{ text: '销量排序', value: 'd' },
                //{ text: '评分最高', value: 'e' },
                //{ text: '评分最低', value: 'f' },
                { text: '点赞排序', value: 'g' },
            ],
            dropdown_isclick:false,
            //endregion
            activeIndex1:null,
            activeIndex2:'0',
            input:null,
            product:{
                id:1,
                name:"测试",
                price:999,
                photo:'p1.webp',
                create_tiem:"2022-12-2"
            },
            currentPage:0,
            PageSize:8,
            tableData:[],
            TotalPage:0,
            IsTableLoading:true,
            FName:null,
            FType:null,
            mobile:{
                home_visitors:null
            }

        }
    },
    methods:{
        dropdown_item_change(v){
            console.log(v)
            this.FType=(v==0?null:v)
            this.currentPage = 1
            this.getproduct()
        },
        dropdown_item_change2(v){
            this.tag_int_local = (v==0?null:v)
        },
        darkenColor(hex, percent) {
            // 移除 # 符号（如果存在）
            hex = hex.replace(/^#/, '')
            
            // 将十六进制转换为 RGB
            let r = parseInt(hex.substr(0, 2), 16)
            let g = parseInt(hex.substr(2, 2), 16)
            let b = parseInt(hex.substr(4, 2), 16)
            
            // 计算加深后的值
            r = Math.floor(r * (100 - percent) / 100)
            g = Math.floor(g * (100 - percent) / 100)
            b = Math.floor(b * (100 - percent) / 100)
            
            // 转换回十六进制
            return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`
        },
        extractText(input) {
            let result
            // 移除形如 ![](URL) 的图片链接
            input = input.replace(/!\[.*?\]\(.*?\)/g, '').replace(/\[.*?\]\(.*?\)/g, '<br>');


            // 移除形如 # 123\n 的
            result = input.replace(/# .*?\n/g, '');

            // 移除形如 :::123 ::: 的
            result = input.replace(/:::.*?\n/g, '');

            
            // 移除换行符 \n
            result = result.replace(/\n+/g, '<br>');
            
            // 移除其他你想忽略的符号，比如 [] 和 () 及其内容
            //result = result.replace(/\[.*?\]/g, '');
            //result = result.replace(/\(.*?\)/g, '');
            
            // 移除多余的空格
            result = result.replace(/\s+/g, ' ').trim();
            
            // 移除内嵌视频
            result = result.replace(/<iframe.*?<\/iframe>/gi, '')

            result = result.replace(/^(<br>)+/i, '');

            return result;
        },
        onMouseEnter(event) {
            this.initialX = event.clientX;
        },
        onMouseMove(event) {
            if(this.donghua)this.donghua=''
            //console.log(this.backgroundPositionX)
            const windowWidth = window.innerWidth;
            const currentX = event.clientX;
            const deltaX = currentX - this.initialX;
            const maxOffset = 2.5; // 最大偏移量 vx px
            //console.log("deltaX:"+deltaX)

            this.backgroundPositionX = deltaX * (maxOffset / windowWidth) + this.Xmove
        },
        onMouseLeave() {
            setTimeout(() => {
                this.backgroundPositionX = this.Xmove; // 复位到初始位置
            }, 300); // 监听器延迟 防止复位失败
            
        },
        async getUserLocation() {
            try {
                // 调用第三方 API 获取用户 IP 和地理位置
                //const response = await axios.get('http://ip-api.com/json/?lang=zh-CN');
                //const data = response.data;

                // 返回位置字符串
                //const country = data.countryCode === 'CN' ? '国内' : '海外';
                //console.log( '访客来源：'+country)

                // 根据国家获取首页图
                const response2 = await axios.get('/data-result/all')
                //if(data.countryCode !== 'CN')this.images = response2.data.en_list
                //else 
                this.images = response2.data.cn_list

                // 添加公用首页图
                // 添加公用首页图
                this.images = this.images.concat(response2.data.co_list);

            } catch (error) {
                console.error('Error fetching user location:', error);
                console.log( '访客来源：未知')
            } finally{
                this.getRandomImage(null)
            }
        },
        // 从事件总线接收 分类博客事件
        product_page_FType_Event(index){
            this.FType = index
            this.tag_int_local = null
            this.getproduct()
        },
        getRandomImage(failedIndices,image_list) {
            if(!failedIndices){
                failedIndices = new Set();
            }

            if (failedIndices.size === image_list.length) {
                console.error('All images failed to load.');
                return;
            }

            let randomIndex;
            let imageUrl;
            let imageUrlBGposition;

            // 随机选择一个不在 failedIndices 集合中的图片
            do {
                randomIndex = Math.floor(Math.random() * image_list.length);
                imageUrl = image_list[randomIndex].photo_url;
                if(imageUrl===this.selectedImage)failedIndices.add(randomIndex) // 防止摇到只剩this.selectedImage===imageUrl
                imageUrlBGposition = image_list[randomIndex].background_position;
            } while (failedIndices.has(randomIndex) || this.selectedImage===imageUrl); // 摇到之前失败的图 或 摇到重复现在的图

            console.log(randomIndex);

            // 创建一个新的 Image 对象来检查图片的加载状态
            const img = new Image();
            let timeoutId;

            img.onload = () => {
                clearTimeout(timeoutId); // 清除定时器
                console.log('onload');
                this.selectedImage = imageUrl;
                this.selectedImageBGposition = imageUrlBGposition;
                this.$nextTick(() => { // 这里只是为了获取到selectedImage后更新模版
                    // 确保 DOM 更新完成后执行的代码
                    console.log('DOM 已更新');
                });
            };

            img.onerror = () => {
                clearTimeout(timeoutId); // 清除定时器
                console.log('onerror');
                failedIndices.add(randomIndex); // 记录失败的图片索引
                this.getRandomImage(failedIndices,image_list);
            };

            // 设置加载超时时间为5秒
            timeoutId = setTimeout(() => {
                console.log('timeout');
                img.onerror();
            }, 5000); // 5000 毫秒 = 5 秒

            img.src = imageUrl; // 设置 src 属性，触发加载
        },
        getRandomImage2(failedIndices, image_list) {
            if (!failedIndices) {
                failedIndices = new Set();
            }

            if (failedIndices.size === image_list.length) {
                console.error('所有图片加载失败。');
                return Promise.reject('所有图片加载失败。');
            }

            let randomIndex;
            let imageUrl;
            //let imageUrlBGposition;

            return new Promise((resolve, reject) => {
                // 随机选择一个不在 failedIndices 集合中的图片
                // 随机选择一个不在 failedIndices 集合中的图片
                do {
                    randomIndex = Math.floor(Math.random() * image_list.length);
                    imageUrl = image_list[randomIndex].photo_url;
                    if(imageUrl===this.selectedImage)failedIndices.add(randomIndex) // 防止摇到只剩this.selectedImage===imageUrl
                    
                } while (failedIndices.has(randomIndex) || this.selectedImage===imageUrl); // 摇到之前失败的图 或 摇到重复现在的图

                console.log(randomIndex);

                // 创建一个新的 Image 对象来检查图片的加载状态
                const img = new Image();
                let timeoutId;

                img.onload = () => {
                    clearTimeout(timeoutId); // 清除定时器
                    console.log('onload');
                    this.$nextTick(() => { // 这里只是为了获取到selectedImage后更新模版
                        // 确保 DOM 更新完成后执行的代码
                        console.log('DOM 已更新');
                    });
                    resolve(imageUrl);
                };

                img.onerror = () => {
                    clearTimeout(timeoutId); // 清除定时器
                    console.log('onerror');
                    failedIndices.add(randomIndex); // 记录失败的图片索引
                    this.getRandomImage2(failedIndices, image_list).then(resolve).catch(reject);
                };

                // 设置加载超时时间为5秒
                timeoutId = setTimeout(() => {
                    console.log('timeout');
                    img.onerror();
                }, 5000); // 5000 毫秒 = 5 秒

                img.src = imageUrl; // 设置 src 属性，触发加载
            });
        },
        // 筛选框关闭触发
        dropdown_closed(value){
            console.log(value)
            if(this.dropdown_isclick){
                this.getproduct()
                this.dropdown_isclick = false
            }
        },
        // vant手机划到底部时触发
        async onLoad() {
            console.log("滚到底部，触发加载")
            this.currentPage ++
            if(this.currentPage>this.pages)return
            await axios.get('product/page',{
                params: {
                    currentPage: this.currentPage,
                    PageSize: this.PageSize,
                    FName:this.FName,
                    FType:this.FType,
                    value2:this.value2,
                    tag_int:this.tag_int_local,
                    showAll:this.showAll
                }
            }).then(response=>{
                this.init_tableData_photo_url(response)
            }).catch(error=>{
                console.log(error)
            }) 
        },
        async init_tableData_photo_url(response) {
            // format新列表图
            for (const obj of response.data.data.records) {
                console.log('obg:'+obj.photo_url)
                if(obj.photo==='noproduct' && !obj.photo_url){
                    obj.photo_url = await this.getRandomImage2(null, this.dataResult.po_list);
                }
            }
            // 拼接
            this.tableData.push(...response.data.data.records)
            this.tag_map    = response.data.map.tag_map
            this.pages      = response.data.data.pages
            this.TotalPage  = response.data.data.total
            this.mobile.home_visitors = response.data.map.home_visitors
            this.IsTableLoading = false

            // 数据全部加载完成
            if (response.data.data.current >= response.data.data.pages) {
                this.finished = true;
            }
            // 加载状态结束
            this.loading = false;

        },
        adjustArrowPosition() {
            console.log('adjustArrowPosition')
            // 使用类名选择器找到所有的箭头元素
            const arrows = document.querySelectorAll('.el-carousel__arrow');
      
            arrows.forEach(arrow => {
                // 动态计算并设置箭头的位置
                // 例如，根据某个元素的尺寸或直接使用计算好的值
                arrow.style.top = `${this.$store.state.CURRENT_HEIGHT * 0.15}px`;
            });
        },
        // 点商品分类
        handleSelect(key) {
            // 0 首页 1 分类 2 标签
            if((key==="0"||key===0 ) && this.FType){
                this.FType=null
                this.currentPage = 1
                this.getproduct()
            }

        },
        // 拿页
        getproduct(){
            this.tableData = []
            this.IsTableLoading = true
            this.finished = false
            this.currentPage=0
            this.pages=2
            this.onLoad()
            console.log(" getproduct()!")
            
        },
        // 点商品
        cardclick(id,comment_num){
            console.log("clicked")
            let num=0
            if(comment_num)num = comment_num
            this.$router.push(`/product?id=${id}&comment_num=${num}`)
        },
        goto4399(){
            console.log(4399)
            window.open('https://www.4399.com', '_blank');
        },
        // 页容量变化
        handleSizeChange(val) {
            this.PageSize = val
            this.getproduct()
        },
        // 点搜索
        clicksearch(input){
            this.input = input
            this.FName = this.input
            this.getproduct()


        },
        // mobile
        mobileTagonClick(val1){
            console.log(val1)
        },
        async getdataresult(){
            await axios.get('/data-result/all')
                .then(response=>{
                    console.log(response.data)
                    this.dataResult = response.data
                    this.init_option1m_option2m()
                })
        },
        // 手机分类标签
        init_option1m_option2m(){
            axios.get(`/data-result/type_count`).then(response=>{
                console.log(response.data)
                const type_trans = response.data.reduce((acc, item) => {
                    acc[item.type] = parseInt(item.count, 10); // 将 count 转换为整数并存入 acc 对象
                    return acc; // 返回累加器给下一个迭代
                }, {});

                this.init_option1m_option2m2(type_trans)
            })         
        },
        init_option1m_option2m2(type_trans){
            this.option1m=[{ text: '分类', value: 0 }]
            this.option2m=[{ text: '标签', value: 0 }]
            const fenlei_map = this.dataResult.fenlei_map
            const biaoqian_map = this.dataResult.biaoqian_map
            
            Object.keys(fenlei_map).forEach(key => {
                this.option1m.push({ text: fenlei_map[key]+" ( "+(type_trans[key]?type_trans[key]:0)+" ) ", value: key ,transValue: type_trans[key] || 0})
            })
            Object.keys(biaoqian_map).forEach(key => {
                this.option2m.push({ text: biaoqian_map[key], value: key })
            })
            // 根据 transValue 进行排序
            this.option1m.sort((a, b) => b.transValue - a.transValue)
        },
        async init(){
            await this.getdataresult()
            this.images = this.dataResult.cn_list
            if(this.dataResult.other_stuff_map.po_add_cn)this.images = this.images.concat(this.dataResult.po_list);
            this.getRandomImage(null,this.images)
            //this.getUserLocation();
            //this.getproduct()
        }

    },
    created(){
        if(this.$route.query.FType){
            this.FType = this.$route.query.FType
            this.value1m = this.$route.query.FType
        }
        
    },
    watch:{
        // 移动端触发翻页
        // eslint-disable-next-line no-unused-vars
        tag_int_local:function(){
            this.getproduct()
        }
    },
    mounted() {
        this.init()
        this.$store.state.zhezhao_show = false
        // 导航栏透明化
        const root = document.documentElement;
        root.style.setProperty('--background-color2', 'transparent');

        
        this.adjustArrowPosition();
        // 监听窗口resize事件，以便动态调整
        window.addEventListener('resize', this.adjustArrowPosition);
        
        // 创建节流后的函数，并绑定到实例上
        this.handleMouseMove = throttle(this.onMouseMove, 100);
        
    },
    beforeDestroy() {
    // 组件销毁时移除事件监听器
        window.removeEventListener('resize', this.adjustArrowPosition);
    }
}
</script>

<style scoped>
.mybordertt {
    border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.514), 0 0 6px rgba(0, 0, 0, 0.377);
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 添加过渡效果 */
  transition: background-position 0.3s;
}
  .mybordert {
    border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.514), 0 0 6px rgba(0, 0, 0, 0.377);
  transition: transform 0.3s ease, box-shadow 0.3s ease, background-image 1s ease; /* 添加过渡效果 */
  background-color: var(--mybordert-bg);
  display: flex;
  margin: 10vh auto;
  height:30vh;
  max-height: 30vh;
  min-height:200px;
  width: 70vw;
  min-width:800px;
  position: relative;
}
.mybordert:hover {
    border-radius: 20px;
  transform: scale(1.03); /* 放大效果 */
  box-shadow: 0 10px 20px var(--shadow1-color); /* 悬浮时增加阴影 */
}

.card-type{
    position: absolute;
    top: 5vh;
    margin-left: -35px;
    padding: 7px 0;
    width: 120px;
    /*background-color: #555;*/
    color:white;
    font-weight: bold;
    font-size: 1rem; 
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.514), 0 0 6px rgba(0, 0, 0, 0.377);
    z-index: 2;
    display: flex;
    justify-content: center;
    align-items: center;
}
.card-type-mini{
    position: absolute;
    top:0px;
    padding: 2px;
    left:-5px;
    /*background-color: #555;*/
    color:white;
    font-weight: bold;
    font-size: 0.7rem; 
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.514), 0 0 6px rgba(0, 0, 0, 0.377);
    z-index: 2;
    display: flex;
    text-align: center;
    justify-content: center;
    align-items: center;
}
.card-type-triangle {
    position: absolute;
    bottom: -23.5px;
    left: 0;
    width: 0;
    height: 0;
    border-style: solid;
    border-width: 12px 17.5px 12px 17.5px; /* 顺序是：上、右、下、左 */
    z-index: 0;
    /*border-color:  #555 #555 transparent transparent;  设置颜色 */
}
.card-typetriangle {
    position: absolute;
    top: 5vh;
    margin-left: -35px;
    margin-top: 35px;
    border-style: solid;
    border-width: 12px 18px 12px 18px; /* 顺序是：上、右、下、左 */
    z-index: 0;
    /*border-color:  #555 #555 transparent transparent;  设置颜色 */
}

.card-out{
  background-color:gray;
  background-size: cover; /* 调整背景图片大小以填充容器 */
  background-position:center; /* 将背景图片顶部对齐 */
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
  display:flex;
  justify-content: center;
  align-items: center;
  width: 65%;
  color:white;
  position: relative;
  cursor: pointer;
  margin: 0;
  box-shadow: 0 2px 4px var(--shadow1-color), 0 0 6px var(--shadow2-color);
  z-index: 1;
}

.card-right{
    width: 35%;
    position: relative;
    display: flex;
  flex-direction: column;
}
.card-tag{
    height: 10%;
    width:100%;
    display: flex;
    margin: 8px 10% 5px;
    padding: 0;
    align-items: center;
    overflow: hidden;
}
.card-tag-item{
    margin-right: 10px;
    font-weight: bolder;
    color: rgb(121, 121, 121);

}

.card-right-out{
    width: 80%;
    max-height: 60%;
    margin: 0 10%;
    position: relative;
}
.card-right-out-in{
    max-height: 18vh;
    text-align: left;
    overflow:hidden;
}

.card-right-down{
    display: flex;
    flex-wrap: nowrap;

    height: 20%;
    line-height: 30px;
    margin: 0 10%;
    margin-top: auto;
    justify-content: space-between;
    align-items: center;
    overflow: hidden;
    justify-self: end;
}

.card-detail2{

  font-weight: bold;
    font-size: 1rem;    
    color:rgba(0, 0, 0, 0.603);
    margin-right: 15px;
}
.card-create-time2{

  font-weight: bold;
    text-align: right;
    color:rgba(0, 0, 0, 0.603);
}



.card{
  flex-grow: 1;
}
.card-title{
  font-size: 2rem;
}
.card-info{
  font-size: 1.5rem;
}

.card-detail{
  position: absolute;
  bottom: 0;
  left: 0;
  margin: 0 0 10px 20px;
  font-weight: bold;
    font-size: 0.9rem;    
}
.card-create-time{
  position: absolute;
  bottom: 0;
  right:0;
  margin: 0 20px 10px 0;
  font-weight: bold;
    font-size: 0.9rem;
}

.bgphoto{
  background-size: cover; /* 调整背景图片大小以填充容器 */
  background-position:top; /* 将背景图片顶部对齐 */
  background-size: 105%;
  width: 100%;
  height: calc(100vw / 2.2);
  max-height: 50vh;
  display: flex;
  position: relative;
  justify-content: center;
  align-items: center;
}

.xiba{
  color:aliceblue;
  font-size: 4.5rem;
  line-height: 1.5;
  margin-bottom: .75rem;
  font-weight: 600;
  position: relative;
}
.xiba-sub{
    position: absolute;
    margin-top: 10px;
    font-size: 1.5rem;
    width: 100%;
    text-align: center;
}
.xiba-sub-sub{
    position: absolute;
    margin-top: 10px;
    font-size: 1.2rem;
    width: 100%;
    text-align: center;
}
.van-card__price {
  position: absolute;
  right: 44%;

}

  
  .el-carousel__arrow {
    top: 50%;
}


  .desktop .el-carousel__item h3 {
    color: #475669;
    font-size: 18px;
    opacity: 0.75;
    /*line-height: 300px;*/
    margin: 0;
  }

  .mobile .el-carousel__item h3 {
    color: #694747;
    font-size: 18px;
    opacity: 0.75;
    line-height: 30px;
    margin: 0;
  }
  
  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }
  
  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }



.xiba-version{
    position: absolute;
    margin: 0 10px 5px 0;
    bottom: 0;
    right: 0;
    color:white;
    font-weight: bold;
    font-size: 0.9rem;
}

.jiantou{
    position: absolute;
    margin: 0 0px 15px 0;
    bottom: 0;
    right: 50%;
    transform: translateX(50%);
}

.xiba-version-num{
    text-align: center;
    font-size: 0.85rem;
}

</style>

<style>
.van-tabs__line{
    background-color: transparent;
}
</style>
