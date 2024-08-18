// 一些全局方法
import axios from './index'
import store from '@/store'

export function getuseroption(id){
    axios.get('user-option/get?id='+id)
        .then(response=>{
            if(response.data.code){
                // 初始化 本地设置
                store.state.option.show_product_scale_win = response.data.data.show_product_scale_win
                console.log('user-option success')
            }else
                this.$message.error(response.data.msg)
        })
}