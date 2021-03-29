import signUp from './images/signUp.svg'
import './signup.css'
import {React, Component} from 'react'
class SignUp extends Component {

    state = {
        email: '',
        password: '',
        role: '',
        address: '',
        city: '',
        phoneNumber: ''
    };

    handleChange = (event) => {
        this.setState(
            {
                [event.target.name]: event.target.value
            }
        );
    }

    handleSubmit = event => {
        event.preventDefault();
    
        const user = {
            email: this.state.email,
            password: this.state.password,
            role: this.state.role,
            address: this.state.address,
            city: this.state.city,
            phoneNumber: this.state.phoneNumber
        };

        console.log(user);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/register', requestOptions)
            .then(response => response.json())
            .then(data => console.log(data));
    }

    render(){
        return (
        <div className="Login">
           <div className="container">

                <div className="row">
                    <div className="col illustration2">
                        <h2><center>Tous nos services au bout du doigt</center></h2>
                        <img src={signUp} className="img-fluid" alt="Logo" /> 
                    </div>
                    <div className="col SignupForm">
                        <h3> <center>Complete the form below</center></h3>
                        <form onSubmit={this.handleSubmit}>
                            <div className="form-group">
                                <label for="exampleInputEmail1">Email address</label>
                                <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" name="email" placeholder="Enter email" onChange={this.handleChange} required/>
                        
                            </div>
                            <div className="form-group">
                                <label for="exampleInputPassword1">Password</label>
                                <input type="password" className="form-control" id="exampleInputPassword1"  name="password" placeholder="Password" onChange={this.handleChange} required/>
                            </div>
                            <div className="form-group">
                                <label for="exampleInputPassword1">Repeat your Password</label>
                                <input type="password" className="form-control" id="exampleInputPassword1" placeholder="Repeat your Password" />
                            </div>

                            <div className="form-group">
                                <label for="exampleInputEmail1">Address</label>
                                <input type="texte" className="form-control"   placeholder="Address" name="address" onChange={this.handleChange}/>
                            </div>
                            
                            <div className="row">
                                <div className="col form-group">
                                    <label for="exampleInputEmail1">City</label>
                                    <input className="form-control" type="text" placeholder="City" name="city" onChange={this.handleChange} />
                                </div>
                                <div className="col form-group">
                                    <label for="exampleInputEmail1">Phone Number</label>
                                    <input className="form-control" type="text" placeholder="Phone number" name="phoneNumber" onChange={this.handleChange}/>
                                </div>
                            </div>
                            <div className="form-check">
                                    <input className="form-check-input" type="radio" name="role" onChange={this.setRole} id="exampleRadios1" value="customer"  onChange={this.handleChange} checked={this.state.role === "customer"}  />
                                    <label className="form-check-label" for="exampleRadios1">
                                        I’m a client, and I have a job that needs to be done.
                                    </label>
                                </div>
                                <div className="form-check">
                                    <input className="form-check-input" type="radio" name="role" id="exampleRadios2" value="freelancer" onChange={this.handleChange} checked={this.state.role === "freelancer"} />
                                    <label className="form-check-label" for="exampleRadios2">
                                        I’m a freelancer, and I want to showcase my services.
                                    </label>
                                </div>
                            <br />
                            <span className="signupButton"><button type="submit" className="btn btn-primary btn-lg btn-block" >Sign Up</button></span>
                        </form>
                    </div>
                    
                </div>

                <div className="row">
                    
                    <div className="col">
                        
                    </div>
                    
                </div>
            </div>
        </div>
        
    ) 
    }
    ;}

export default  SignUp;