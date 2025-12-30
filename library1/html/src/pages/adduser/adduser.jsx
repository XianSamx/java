import { useState } from "react"
import"./adduser.css"
import { Form } from "react-bootstrap"
import { Button } from "react-bootstrap"

const AddUser = () => { 
    const[fromData,setFormData]=useState({
        name:"",
        password:"",
    })
}

const handleInputChange=(e)=>{
    const{name,value}=e.target
    setFormData({...fromData,[name]:value})
}

return ( 
    <>
    <div className="adduser">
        <h1>用户注册</h1>
        <Form> 
            <Form.Group controlId="formName">
                <Form.Control type="text" 
                name="name" 
                placeholder="请输入用户名"
                value={fromData.name} 
                onChange={handleInputChange}/>
            </Form.Group>
        </Form> 
    </div>    
    </>
)
