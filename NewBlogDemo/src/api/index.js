import axios from '@/utils'

// 暂时没用上
export function logout() {
    axios.get('/user/logout')
        .then(response => {
            console.log(response)
            if (response.data.code) {
                this.$message.success("退出登录成功")
                this.$store.state.IsLogin = false
                this.$root.$emit('logout')
                this.$router.push('/home')
            } else {
                this.$message.error("退出失败：" + response.data.msg)
            }
        })
}
