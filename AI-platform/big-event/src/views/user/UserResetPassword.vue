<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userResetPasswordService } from '@/api/user.js'
import { useTokenStore } from '@/stores/token.js'
import useUserInfoStore from '@/stores/userInfo.js'
import { useRouter } from 'vue-router'

const formRef = ref()
const router = useRouter()
const tokenStore = useTokenStore()
const userInfoStore = useUserInfoStore()

const passwordData = ref({
    old_pwd: '',
    new_pwd: '',
    re_pwd: ''
})

const checkRePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入新密码'))
    } else if (value !== passwordData.value.new_pwd) {
        callback(new Error('两次输入的新密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    old_pwd: [
        { required: true, message: '请输入原密码', trigger: 'blur' },
        { pattern: /^\S{5,16}$/, message: '密码长度应为5-16位且不能包含空格', trigger: 'blur' }
    ],
    new_pwd: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { pattern: /^\S{5,16}$/, message: '密码长度应为5-16位且不能包含空格', trigger: 'blur' }
    ],
    re_pwd: [
        { validator: checkRePassword, trigger: 'blur' }
    ]
}

const resetPassword = async () => {
    await formRef.value.validate()
    const result = await userResetPasswordService(passwordData.value)
    ElMessage.success(result.msg ? result.msg : '密码修改成功，请重新登录')

    tokenStore.removeToken()
    userInfoStore.removeInfo()
    router.push('/login')
}

const resetForm = () => {
    formRef.value.resetFields()
}
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>重置密码</span>
            </div>
        </template>

        <el-row>
            <el-col :span="12">
                <el-form ref="formRef" :model="passwordData" :rules="rules" label-width="120px" size="large">
                    <el-form-item label="原密码" prop="old_pwd">
                        <el-input v-model="passwordData.old_pwd" type="password" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="new_pwd">
                        <el-input v-model="passwordData.new_pwd" type="password" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="确认新密码" prop="re_pwd">
                        <el-input v-model="passwordData.re_pwd" type="password" show-password></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="resetPassword">提交修改</el-button>
                        <el-button @click="resetForm">重置表单</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
    </el-card>
</template>

<style lang="scss" scoped>
.page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}
</style>