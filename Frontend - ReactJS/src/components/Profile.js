import HorizontalNav from "./HorizontalNav";
import {React, Component} from 'react'
import { withRouter } from "react-router-dom";
import './profile.css'
import profilePng from './images/profile.png'
import newDiploma from './images/newDiploma.svg'
import diplomas from './images/diplomas.svg'

class Profile extends Component{
    state = {
        email: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        birthDate: '',
        city: '',
        address: '',
        activity: '',
        minimumWage: '',
        diplomas: [],
        newDiploma: {
            name: '',
            acquisitionDate: Date.now(),
            field: ''
        }
    }

    handleProfileUpdate = (event) => {
        event.preventDefault();

        const user = {
            firstName: this.state.firstName,
            lastName: this.state.lastName,
            phoneNumber: this.state.phoneNumber,
            birthDate: this.state.birthDate,
            city: this.state.city,
            address: this.state.address,
            activity: this.state.activity,
            minimumWage: this.state.minimumWage
        };

        console.log(user);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/profile/update', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
            }
        );
    }

    handleDiplomaUpdate = (event, index) => {
        event.preventDefault();

        const diploma = this.state.diplomas[index];

        console.log(diploma);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(diploma)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/diplomas/update', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
            }
        );
    }

    handleProfileChange = (event) => {
        this.setState(
            {
                [event.target.name]: event.target.value
            }
        )
    }

    handleDiplomaChange = (event, index) => {
        this.setState(
            {
                diplomas: [
                    ...this.state.diplomas.slice(0, index),
                    {
                        ...this.state.diplomas[index],
                        [event.target.name]: event.target.value
                    },
                    ...this.state.diplomas.slice(index + 1)
                ]
            }
        );
    }

    handleNewDiplomaChange = (event) => {
        console.log(event);
        this.setState({
            newDiploma: {
                ...this.state.newDiploma,
                [event.target.name]: event.target.value
            }
        })
    }

    handleAddDiploma = (event) => {
        event.preventDefault();
        
        const newDiploma = {...this.state.newDiploma}

        console.log(newDiploma);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newDiploma)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/diplomas/create', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
                newDiploma.id = data.id;
                this.setState(
                    {
                        diplomas: [
                            ...this.state.diplomas,
                            newDiploma
                        ],
                        newDiploma: {
                            name: '',
                            acquisitionDate: new Date(),
                            field: ''
                        }
                    }
                )
            }
        );
    }

    handleDiplomaDelete = (event, index) => {
        event.preventDefault();

        const diploma = this.state.diplomas[index];

        const requestOptions = {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: diploma.id })
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/diplomas/delete', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
                this.setState({
                    diplomas: [
                        ...this.state.diplomas.slice(0, index),
                        ...this.state.diplomas.slice(index + 1)
                    ]
                })
            });
    }

    handleLogout = (event) => {
        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/logout', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            this.props.rest.logout();
            this.props.history.push('/login');
        });
    }

    formatDate = (date) => {
        if (date == null || date === '')
            return '';
        
        let d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();
    
        if (month.length < 2) 
            month = '0' + month;
        if (day.length < 2) 
            day = '0' + day;
    
        return [year, month, day].join('-');
    }

    componentDidMount() {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/profile', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            if (data != null) {
                this.setState(
                    {
                        email: data.email,
                        firstName: data.firstName,
                        lastName: data.lastName,
                        phoneNumber: data.phoneNumber,
                        birthDate: this.formatDate(data.birthDate),
                        city: data.city,
                        address: data.address,
                        activity: data.activity,
                        minimumWage: data.minimumWage,
                        diplomas: JSON.parse(data.diplomas)
                    }
                );    
                    localStorage.setItem('FirstName', data.firstName);
                    
                    localStorage.setItem('LastName', data.lastName);
                
            }
        });
    }

    render(){
        const diplomaList = () => {
            return this.state.diplomas.map((diploma, index) => {
                return (
                    <form onSubmit={(e) => {this.handleDiplomaUpdate(e, index)}} key={index}>
                        <div className="">
                            <div className=" ">
                                <div className="">
                                    <div className="row">
                                        <div className="col">
                                            <h3 className="freelancerProfile">Diploma</h3>
                                            <button type="submit" className="btn btn-primary floatRight">Save</button>
                                            <button type="button" className="btn btn-danger floatRight" onClick={(e) => {this.handleDiplomaDelete(e, index)}}>Delete</button>
                                        </div>
                                    </div>    
                                    <hr />   
                                    <div className="row"> 
                                        <div className="col">
                                            <label>Name</label>
                                            <input name="name" value={diploma.name} onChange={(e) => {this.handleDiplomaChange(e, index)}} className="form-control" required />
                                        </div>
                                    </div>    
                                    <hr />
                                    <div className="row">
                                        <div className="col-6">
                                            <label>Acquisition Date</label>
                                            <input name="date" type="date" value={this.formatDate(diploma.acquisitionDate)} onChange={(e) => {this.handleDiplomaChange(e, index)}} className="form-control" required />
                                        </div>
                                        <div className="col-6">
                                            <label>Field</label>
                                            <input name="field" value={diploma.field} onChange={(e) => {this.handleDiplomaChange(e, index)}} className="form-control" required />
                                        </div>
                                    </div>
                                    <hr />
                                </div>
                            </div>
                        </div>
                    </form>
                );
            })
        };

        return <div className="row">
                <div className="col-2">
                    <HorizontalNav logout={this.props.rest.logout}/>
                </div>
                <div className="col Content ">
                    <div className="row ">
                        <div className="col ProfileHeader">
                            <h2 className="freelancerProfile"></h2>
                            <div className="floatRight">
                                
                            </div>
                        </div>
                    </div>
                    
                    <div className="row">
                        <div className="col-4 profileIllustration">
                            <img src={profilePng} className="img-fluid" alt="Profile" />
                        </div>
                        <div className="col-7 Content PersonalDetails">
                            
                            <form onSubmit={this.handleProfileUpdate}>
                                <div >
                                    <div >
                                        <div className="row">
                                            <div className="col">
                                                <button type="submit" className="btn btn-success floatRight">Save</button>
                                                <h5 className="freelancerProfile"> Personal Details <br/> {this.state.email} </h5>
                                                <p></p>
                                            </div>
                                        </div>
                                        
                                        
                                        <div className="row">
                                            <div className="col-6">
                                                <label>First Name</label>
                                                <input name="firstName" value={this.state.firstName} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                            <div className="col-6">
                                                <label>Last Name</label>
                                                <input name="lastName" value={this.state.lastName} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                        </div>
                                        <hr />
                                        <div className="row">
                                            <div className="col">
                                                <label>Birth Date</label>
                                                <input name="birthDate" type="date" value={this.formatDate(this.state.birthDate)} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                        </div>

                                        <hr />
                                        <div className="row">
                                            <div className="col-6">
                                                <label>Address</label>
                                                <input name="address" value={this.state.address} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                            <div className="col-6">
                                                <label>City</label>
                                                <input name="city" value={this.state.city} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                        </div>
                                        <hr />
                                        <div className="row">
                                            <div className="col">
                                                <label>Phone Number</label>
                                                <input name="phoneNumber" type="tel" value={this.state.phoneNumber} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                            
                                        </div>
                                        <hr />
                                        <div className="row">
                                            <div className="col-6">
                                                <label>Activity</label>
                                                <input name="activity" value={this.state.activity} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                            <div className="col-6">
                                                <label>Minimum Wage</label>
                                                <input name="minimumWage" value={this.state.minimumWage} onChange={this.handleProfileChange} className="form-control" required />
                                            </div>
                                        </div>
                                        
                                    </div>
                                </div>
                            </form>
                        </div>
                        
                    </div>
                    <div className="row Content ">
                        <div className="col-4">
                                <img src={newDiploma} className="img-fluid" alt="Profile" />
                            </div>
                        <div className="col-8  PersonalDetails">
                            {diplomaList()}
                        </div>
                    </div>
                    
                    
                        <div className="row Content ">
                            <div className="col-4">
                                <img src={diplomas} className="img-fluid" alt="Profile" />
                            </div>
                            <div className="col-8  PersonalDetails">
                                <div class="row">
                                    <div className="col">
                                        <h3 className="freelancerProfile">Add new Diploma</h3>
                                        <button type="button" className="btn btn-primary floatRight" onClick={this.handleAddDiploma}>Add</button>
                                    </div>
                                    <hr /> 
                                </div>
                                <div className="row "> 
                                    <div className="col">
                                            <label>Name</label>
                                        <input name="name" className="form-control" value={this.state.newDiploma.name} onChange={this.handleNewDiplomaChange} required />
                                    </div>
                                    <hr />
                                </div>
                                <div className="row ">
                                    <div className="col-6">
                                        <label>Acquisition Date</label>
                                        <input name="acquisitionDate" type="date" className="form-control" value={this.formatDate(this.state.newDiploma.acquisitionDate)} onChange={this.handleNewDiplomaChange} required />
                                    </div>
                                    <div className="col-6">
                                        <label>Field</label>
                                        <input name="field" className="form-control" value={this.state.newDiploma.field} onChange={this.handleNewDiplomaChange} required />
                                    </div>
                                </div>
                            </div>
                            
                        </div> 

                          
                    
                    
                </div>
        </div>
    }
}

export default withRouter(Profile);