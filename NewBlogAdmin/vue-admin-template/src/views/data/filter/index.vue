<template>
  <div style="color:#909399;">
    <el-dropdown trigger="click" placement="bottom" :hide-on-click=false
                v-if="fliter_list?.some(str => column?.label.includes(str))" 
                @command="handleCommand"
                @visible-change="(isVisible) => handleVisibleChange(isVisible, column?.prop)">
        <span class="el-dropdown-link">
            <span :style="this.search_add_last_time[this.column.prop]?
            Object.keys(this.search_add_last_time[this.column.prop]).length?
            'color:rgba(28, 114, 225, 0.87);':''
            :''">{{ column?.label }}</span><i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu class="menu" slot="dropdown" >
            <el-dropdown-item  class="mysearch" @click.stop="" command="search" divided>
                <input @click.stop="" v-model="search_text" type="text" @input="onInput">
            </el-dropdown-item>
            <div v-loading="search_loading">
                <el-dropdown-item v-for="(item,key) in search_data"  :key="key"
                >
                <span v-if="column?.prop==='type'" style="display: flex; justify-content: space-between;">
                    <span><input class="checkbox" v-model="item.ischecked" @change="handleCheckboxChange($event,item.yuansu)" @click.stop="" type="checkbox"> {{item.yuansu}}({{ fenlei_map?fenlei_map[item.yuansu]:'' }})</span><span>{{ item.count }}</span>
                </span>
                <span v-if="column?.prop==='t_tag_label'" style="display: flex; justify-content: space-between;">
                    <span><input class="checkbox" v-model="item.ischecked" @change="handleCheckboxChange($event,item.yuansu)" @click.stop="" type="checkbox"> {{item.yuansu}}({{ biaoqian_map?biaoqian_map[item.yuansu]:'' }})</span><span>{{ item.count }}</span>
                </span>
                <span v-else style="display: flex; justify-content: space-between;gap: 20px;">
                    <span><input class="checkbox" v-model="item.ischecked"  @change="handleCheckboxChange($event,item.yuansu)" @click.stop="" type="checkbox">{{ item.yuansu }}</span><span>{{ item.count }}</span>
                </span>
                </el-dropdown-item>
            </div>
        </el-dropdown-menu>
    </el-dropdown>
    <div v-else>
        {{ column?.label }}
    </div>

    <!--接收update的总线事件-->
    <rootEvent :event_name="`update_`+column.prop" @rootEvent="update" />

  </div>
</template>

<script>
import axios from '@/utils/axios';
import rootEvent from '@/views/data/receivedrootevent/VE'
import { debounce } from 'lodash';

export default {
    components:{
        rootEvent
    },
    props:{
        column:Object,
        fenlei_map:Object,
        biaoqian_map:Object,
        biao:String,
        fliter_list:Array,
        
        yaunsuListMap:Object,
        currentPage:Number,
        PageSize:Number,
        value2:String,
        dialogdata3:Object
    },
    data() {
        return {
            search_text:null,
            search_loading:true,
            search_data:[],
            search_add:{},
            search_add_last_time:{},// 维护checkbox状态用的 新点删由add del负责
            search_del:{},
            checkbox_count:0,
            Checkbox_click:true
        };
    },
    methods: {
        init_checkbox_bool(){
            console.log('this.search_add[this.column.prop]')
            console.log(this.search_add_last_time[this.column.prop])
            this.search_add_last_time[this.column.prop]?.forEach(item=>{
                const index = this.search_data.findIndex(item2 => item2.yuansu === item.value)
                if(index!==-1){
                    this.$set(this.search_data[index], 'ischecked', true)
                }
            })
        },
        init_checkbox_bool_fail(){
            console.log('this.yaunsuListMap')
            console.log(this.yaunsuListMap)
            this.yaunsuListMap[this.column.prop].forEach(item => {
                const index = this.search_data.findIndex(item2 => item2.yuansu === item.value)
                if(index!==-1){
                    this.$set(this.search_data[index], 'ischecked', true)
                }else{
                    this.$set(this.search_data[index], 'ischecked', false)
                }

            });
        },
        handleCheckboxChange(event,yuansu) {
            const isChecked = event.target.checked;
            if (isChecked) {
                this.handleAction('checked',yuansu);
            } else {
                this.handleAction('unchecked',yuansu);
            }
        },
        handleAction(status,yuansu) {
            this.Checkbox_click=true
            console.log(`Checkbox is ${status}:${yuansu}`);
            if(status==='checked'){
                if(this.search_add[this.column.prop])this.search_add[this.column.prop].push({ 'value':yuansu })
                else this.search_add[this.column.prop]=[],this.search_add[this.column.prop].push({ 'value':yuansu })

                if(this.search_add_last_time[this.column.prop])this.search_add_last_time[this.column.prop].push({ 'value':yuansu })
                else this.search_add_last_time[this.column.prop]=[],this.search_add_last_time[this.column.prop].push({ 'value':yuansu })
            }
            else 
            {
                if(this.search_del[this.column.prop])this.search_del[this.column.prop].push({ 'value':yuansu })
                else this.search_del[this.column.prop]=[],this.search_del[this.column.prop].push({ 'value':yuansu })
                // 减去addmap
                this.search_add_last_time[this.column.prop].forEach(item=>console.log(item.value===yuansu))
                const index = this.search_add_last_time[this.column.prop].findIndex(item=>item.value===yuansu)
                console.log('减去addmap的index:'+index)
                if(index!==-1)this.$delete(this.search_add_last_time[this.column.prop], index)
            }
        },
        handleVisibleChange(bool){
            console.log('handleVisibleChange:'+bool+":")
            if(bool){
                // 显示 
                //(点过box才拿新数据)
                //if(!this.Checkbox_click)return 
                this.search_loading = true
                this.search_add = {}
                this.search_del = {}
                this.search_data = []
                axios.post(`/all/paiming/${this.column.prop}/${this.biao}`,{
                    'yaunsuListMap':this.yaunsuListMap,
                    'biao':this.biao,
                    'currentPage': this.currentPage, 
                    'PageSize': this.PageSize,
                    'value2':this.value2,
                    ...this.dialogdata3,
                    'search_text':this.search_text
                }).then(response=>{
                    if(response.data.code){
                        console.log(response.data.data)
                        this.search_loading = false
                        this.search_data = response.data.data
                        this.search_data.forEach(obj=>{
                            this.$set(obj, 'ischecked', false)
                        })
                        this.Checkbox_click = false
                        this.init_checkbox_bool()
                    }else
                        this.$message.error(response.data.msg)
                })
            }else{
                // 隐藏
                //if(Object.keys(this.search_add).length === 0 && Object.keys(this.search_del).length === 0)return
                if(this.Checkbox_click)this.$emit('selectpagebyadminWithSearch_SetList',this.search_add,this.search_del,this.biao)
            }
            
        },
        handleCommand(){

        },
        update(){
            console.log(this.column.prop+"接收到update")
            this.$forceUpdate(); // 强制更新
        },
        onInput: debounce(function () {
            this.triggerSearch();
        }, 300),
        triggerSearch() {
            // 在这里添加你的查询逻辑
            console.log('Searching for:', this.search_text);
            this.handleVisibleChange(true)
        }
    }
};
</script>

<style scoped>
.menu{
    max-height: 500px;
    min-width: 150px;
    overflow-y: auto;
}

.el-dropdown-menu__item {
    cursor:default;
}
.checkbox{
    margin: 0px 3px 0 0;
    cursor: pointer;
}
.mysearch > input{
    border:1px solid gainsboro;
    height: 1.4rem;
    width: 100%;
}
input:focus {
  outline: 1px solid gainsboro;
}
.el-dropdown-link{
    color:#909399;
    font-size: 14px;
    cursor: pointer;
}
</style>
