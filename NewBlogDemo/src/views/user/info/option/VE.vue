<template>
    <div>

        <div class="block">
            <div v-for="(choice,key) in option" :key="key">
                <van-cell v-if="key!=='id'&&key!=='user_id'&&key!=='create_time'" center :title="option_trans[key]">
                <template #right-icon>
                    <van-switch @change="change" v-model="option[key]" size="24" />
                </template>
                </van-cell>
            </div>
        </div>

    </div>
</template>

<script>
import { Cell,Switch } from 'vant';
import axios from '@/utils'
//import { Dialog } from 'vant';
//import axios from '@/utils'
//import { Icon } from 'vant';


export default {
    components: {
        'van-cell':Cell,
        'van-switch':Switch
    },
    props: {},
    data () {
        return {
            option:{
                //show_product_scale_win:true,
                //subscribe_comment_reply_by_mail
            },
            option_trans:{
                show_product_scale_win:"显示缩放调节小窗",
                subscribe_comment_reply_by_mail:"邮件订阅评论回复"
            }
        }
    },
    methods: {
        get(){
            this.option = null
            axios.get('user-option/get?id='+this.$route.query.id).then(response=>{
                if(response.data.code){
                    this.option = response.data.data
                    this.$store.state.zhezhao_show = false
                    console.log(this.option)
                }else
                    this.$message.error(response.data.msg)

            })
        },
        change(){
            this.$store.state.option = JSON.parse(JSON.stringify(this.option))
            axios.put('user-option/update',this.option).then(response=>{
                if(response.data.code){
                    console.log("option update success")
                }else
                    this.$message.error(response.data.msg)

            })
        }
    },
    mounted(){
        this.get();
        //this.$store.state.zhezhao_show = false 改在get里了
    }
}
</script>

<style scoped>
.van-cell{
    text-align: left;
    background-color: transparent;
}
.block{
    border-radius: 10px;
    margin: 20px;
    background-color: white;
}
</style>
