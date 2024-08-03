<template>
  <div>
    <h1 style="color:var(--ptext-color)">归档</h1>
    <p style="color:var(--ptext-color);margin-top: -10px;margin-bottom: 20px;">共&nbsp;{{ content?.length?content.length:'..' }}&nbsp;篇</p>
    <div v-loading="loading" class="myborder" 
    v-for="(list,key) in dict_of_yearList" :key="key"
    style="text-align: left;padding: 20px 30px;margin-bottom: 5px;"
    >
        <h3>{{key}}</h3>
        <ul>
            <li v-for="(obj,key2) in list" :key="key2" style="padding: 10px;cursor: pointer;display: flex;" @click="$router.push('product?id='+obj.id);">
                <div>{{obj.create_time}}</div>
                <div :style="`width:${maxLength}px;`" ref="fenleiMapDivs">{{dataResult?.fenlei_map?.[obj.type]}}</div>
                <div>{{obj.name}}</div>
            </li>
        </ul>
    </div>
  </div>
</template>

<script>
import axios from '@/utils'
import moment from 'moment'

export default {
    data() {
        return{
            loading:true,
            content:null,
            dataResult:null,
            dict_of_yearList:{}, //元素为每年的列表,
            maxLength:null,
        }
    },
    methods:{
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
            await axios.get('/product/guidang')
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
                if( this.dict_of_yearList[object.create_time.slice(0, 4)]){
                    this.dict_of_yearList[object.create_time.slice(0, 4)].push({'name':object.name,'id':object.id,'create_time':moment(object.create_time).format('MM月DD日'),'type':object.type})
                }else{
                    // 直接this.dict_of_yearList[object.create_time.slice(0, 4)] = [..] vue2不会触发更新
                    this.$set(this.dict_of_yearList,object.create_time.slice(0, 4),[{'name':object.name,'id':object.id,'create_time':moment(object.create_time).format('MM月DD日'),'type':object.type}])
                }
            })
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
    padding: 0 10px;
}
</style>
