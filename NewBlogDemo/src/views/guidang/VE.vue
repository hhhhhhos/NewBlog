<template>
    <div>
        <!-- 电脑 -->
        <div v-if="!this.$store.state.IsMobile">
            <h1 style="color:var(--ptext-color)">归档</h1>
            <p style="color:var(--ptext-color);margin-top: -10px;margin-bottom: 10px;">共&nbsp;{{ content?.length?content.length:'..' }}&nbsp;篇</p>
            <div class="juzhong">   
                <div 
                v-if="type!==-1 && !loading" 
                class="fenlei" :style="`width:${maxLength}px;margin-bottom: 20px;background-color:${dataResult?.fenlei_color_map?.[type]};`">
                <van-icon class="icon_close"  name="close" @click="type=-1,get()"/>
                {{dataResult?.fenlei_map?.[type]}}
                </div>

                <div 
                v-if="is!==null && !loading" 
                class="fenlei" style="margin: 0 0 20px 10px;">
                <van-icon class="icon_close"  name="close" @click="is=null,get()"/>
                {{is}}
                </div>
            </div>
            <div v-loading="loading" style="min-height: 200px;">

                <div class="myborder" 
                v-for="(year,key) in yearListDesc" :key="key"
                style="text-align: left;padding: 20px 30px;margin-bottom: 5px;"
                >
                    <h3>{{year}}</h3>
                    <ul>
                        <ul v-for="(obj,key2) in dict_of_yearList[year]" :key="key2" >
                            <h4 v-if="obj.yue" style="margin-left: 15px;">{{ obj.create_time.slice(0,2) }} 月</h4>
                            <li style="padding: 10px;cursor: pointer;display: flex;" @click="$router.push('product?id='+obj.id);">
                                <div>{{obj.create_time}}</div>
                                <div class="fenlei" :style="`width:${maxLength}px;background-color:${dataResult?.fenlei_color_map?.[obj.type]};`" ref="fenleiMapDivs"
                                @click.stop="type=obj.type,get()">
                                    {{dataResult?.fenlei_map?.[obj.type]}}
                                </div>
                                <div @click.stop="is=content[key2]['is'],get()" >{{ kejian(key2) }}</div>
                                <div style="position: relative;">
                                    {{obj.name}}
                                    <svg v-if="content[key2].is_top" style="position: absolute;right: -30;" t="1722121138299" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4266" width="16" height="16"><path d="M951.296 424.96L1024 352.256 671.744 0 599.04 72.704l70.144 70.656-168.96 168.96a296.96 296.96 0 0 0-286.72 75.264L143.36 458.24 72.704 387.584 0 460.8l245.248 245.248-139.776 139.776 72.704 72.704 140.288-140.288L563.2 1024l72.704-72.704-70.144-70.656 70.144-70.144a296.96 296.96 0 0 0 75.776-287.232l168.96-168.96z" fill="#333333" p-id="4267"></path></svg>
                                </div>
                            </li>
                        </ul>
                    </ul>
                </div>

            </div>

        </div>

        <!-- 手机 -->
        <div v-else>
            <h1 style="color:var(--ptext-color)">归档</h1>
            <p style="color:var(--ptext-color);margin-top: -10px;margin-bottom: 10px;">共&nbsp;{{ content?.length?content.length:'..' }}&nbsp;篇</p>
            <div class="juzhong">   
                <div 
                v-if="type!==-1 && !loading" 
                class="fenlei" :style="`width:${maxLength}px;margin-bottom: 20px;background-color:${dataResult?.fenlei_color_map?.[type]};`">
                <van-icon class="icon_close"  name="close" @click="type=-1,get()"/>
                {{dataResult?.fenlei_map?.[type]}}
                </div>

                <div 
                v-if="is!==null && !loading" 
                class="fenlei" style="margin: 0 0 20px 10px;">
                <van-icon class="icon_close"  name="close" @click="is=null,get()"/>
                {{is}}
                </div>
            </div>
            <div v-loading="loading" style="min-height: 200px;">

                <div class="myborder" 
                v-for="(year,key) in yearListDesc" :key="key"
                style="text-align: left;padding: 20px 10px;margin-bottom: 5px;"
                >
                    <h3>{{year}}</h3>
                    <ul>
                        <ul v-for="(obj,key2) in dict_of_yearList[year]" :key="key2" >
                            <h4 v-if="obj.yue" >{{ obj.create_time.slice(0,2) }} 月</h4>
                            <li style="padding: 10px;cursor: pointer;display: flex;" @click="$router.push('product?id='+obj.id);">

                                <div style="white-space: nowrap; margin-right: 10px;border: 1px solid black;height: 1.2rem;border-radius: 0.2rem;"
                                @click.stop="type=obj.type,get()">
                                    {{dataResult?.fenlei_map?.[obj.type]}}
                                </div>
                                
                                <div style="position: relative;">
                                    {{obj.name}}
                                    <svg v-if="content[key2].is_top" style="position: absolute;right: -30;" t="1722121138299" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4266" width="16" height="16"><path d="M951.296 424.96L1024 352.256 671.744 0 599.04 72.704l70.144 70.656-168.96 168.96a296.96 296.96 0 0 0-286.72 75.264L143.36 458.24 72.704 387.584 0 460.8l245.248 245.248-139.776 139.776 72.704 72.704 140.288-140.288L563.2 1024l72.704-72.704-70.144-70.656 70.144-70.144a296.96 296.96 0 0 0 75.776-287.232l168.96-168.96z" fill="#333333" p-id="4267"></path></svg>
                                </div>
                            </li>
                        </ul>
                    </ul>
                </div>

            </div>

        </div>
  </div>
</template>

<script>
import axios from '@/utils'
import moment from 'moment'
import { Icon } from 'vant';

export default {
    components:{
        'van-icon':Icon
    },
    data() {
        return{
            yue:null,
            loading:true,
            content:null,
            dataResult:null,
            dict_of_yearList:{}, //元素为每年的列表,
            maxLength:null,
            type:-1,
            is:null,
            yearListDesc:null
        }
    },
    methods:{
        kejian(index){
            if(this.content[index].is_show){
                if(this.content[index].is_on_homepage){
                    this.content[index]['is'] = '首页可见'
                    return '首页可见'
                }
                else{
                    this.content[index]['is'] = '分类可见'
                    return '分类可见'
                }
            }else{
                this.content[index]['is'] = '本人可见'
                return '本人可见'
            }
        },
        yueF(yue){
            if(this.yue!==yue){this.yue = yue;return true}
            else return false
        },
        calculateLongestDivLength() {
            let maxLength = 0;
            this.$refs.fenleiMapDivs.forEach(div => {
                const length = div.textContent.trim().length;
                //console.log(length)
                if (length > maxLength) {
                    maxLength = length;
                }
            });
            console.log('最长的div长度是: ', maxLength);
            //return maxLength
            this.maxLength = maxLength * 16
            this.$nextTick({}) // 刷新dom
        },
        async get(){
            this.loading = true
            this.dict_of_yearList = {}
            await axios.get(`/product/guidang?type=${this.type}&is=${this.is}`)
                .then(res=>{
                    this.content = res.data
                    
                })
            await axios.get('/data-result/all')
                .then(response=>{
                    this.dataResult = response.data
                    
                })
            this.format()
            this.$nextTick(() => { // dom之后加载
                this.calculateLongestDivLength()
            });
            this.loading = false
        },
        format(){
            this.content.forEach(object=>{
                const obj = {
                    'name':object.name,
                    'id':object.id,
                    'create_time':moment(object.create_time).format('MM月DD日'),
                    'type':object.type,
                    'yue':this.yueF(object.create_time.slice(5,7))
                }
                if( this.dict_of_yearList[object.create_time.slice(0, 4)]){
                    this.dict_of_yearList[object.create_time.slice(0, 4)].push(obj)
                }else{
                    // 直接this.dict_of_yearList[object.create_time.slice(0, 4)] = [..] vue2不会触发更新
                    this.$set(this.dict_of_yearList,object.create_time.slice(0, 4),[obj])
                }
            })
            console.log(this.dict_of_yearList)
            this.yearListDesc=Object.keys(this.dict_of_yearList).reverse();
            console.log(this.dict_of_yearList)
        },
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.get()
    }
}
</script>

<style scoped>
li:hover{
    background-color: var(--el-loading-mask-bg);
}
li > *{
    margin-right: 10px;
    padding: 0 10px;
    display: flex;
    align-items: center;
}
.fenlei{
    background-color: #555555;
    text-align: center;
    justify-content: center;
    padding: 5px 15px;
    border-radius: 10px;
    color: white;
    position: relative;
}

.icon_close{
    position: absolute;
    top: 0;
    right: 0;
    margin: 1px 2px 0 0;
    cursor: pointer;
    font-size: 12px;
    color: rgb(255, 248, 248);
}
</style>
