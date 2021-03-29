import { React, Component } from "react";
import { withRouter } from "react-router-dom";
import CustomerNavbar from "./CustomerNavbar";
import HorizontalNav from "./HorizontalNav";
import './customerDashboard.css'


class CustomerDashboard extends Component{

    state = {
        items : [],
        description : "",
        city : "",
        activity : "",
        minimumWage : "",
        startDay : "",
        options : [
        { value: 'Plomberie', label: 'Plomberie' }
      ]
    }

    

    componentDidMount() {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/offers/myoffers', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            
            
            this.setState(
                {items : data}
            );
        });
    }

    handleOffer = () => {

        const offer = {
            description: this.state.description,
            city: this.state.city,
            activity: this.state.activity,
            minimumWage: this.state.minimumWage,
            startDay : this.state.startDay
        };

        console.log(offer);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(offer)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/offers/new', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
                this.setState({
                    description : "",
                    city : "",
                    activity : "",
                    minimumWage : "",
                    startDay : ""
                })
                    
            }
        );


    }

    handleOfferChange = (event) => {
        this.setState(
            {
                [event.target.name]: event.target.value
            }
        )
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

    render(){
        const offers = ()=>{
            return this.state.items.map((item) => {
                return (<tr>
                    <th scope="row">{item.id}</th>
                    <td>{item.description}</td>
                    <td>{item.activity}</td>
                    <td>{item.city}</td>
                    <td>{item.minimumWage}</td>
                    <td>{new Date(item.startDay).toLocaleString()}</td>
                    <td>{item.status}</td> 
                </tr>
                )
            })
        }
        return <div className="row">
            <div className="col-2">
                    <HorizontalNav logout={this.props.rest.logout}/>
                </div>
            <div className="col Content">
                <div className="row">
                    <div className="col addNew">
                        <div className="row">
                            <div className="col">
                                <h4 className="floatLeft">Add new Offer</h4>
                                <button type="button" className="btn btn-primary floatRight" onClick={()=>this.handleOffer()}>Add</button>
                            </div>
                        </div>
                        <div className="row addNewContent">
                            <div className="col">
                                <div className="row ">
                                    <div className="col">
                                        <div className="row">
                                            <div className="col">
                                                <div className="form-group">
                                                    <label for="exampleFormControlTextarea1">Description</label>
                                                    <textarea className="form-control" id="exampleFormControlTextarea1" rows="2" name="description" onChange={this.handleOfferChange} value={this.state.description}></textarea>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="row">
                                            <div className="col-6">
                                                <label for="exampleFormControlTextarea1">City</label>
                                                <input className="form-control" type="text" placeholder="Default input" name="city" onChange={this.handleOfferChange}  value={this.state.city}/>
                                            </div>
                                            <div className="col-6">
                                                <label for="exampleFormControlTextarea1">Start Day</label>
                                                <input className="form-control" type="date" onChange={this.handleOfferChange} value={this.formatDate(this.state.startDay)} name="startDay"/>
                                                 {/* this.handleOfferChange} */}
                                            </div>
                                        </div>
                                        <div className="row">
                                            <div className="col-6">
                                                <div className="form-group">
                                                    <label for="exampleFormControlSelect1">Activity</label>
                                                    <select className="form-control" id="activity" onChange={this.handleOfferChange} name="activity" value={this.state.activity}>
                                                    <option></option>
                                                    <option value="Plomberie" >Plomberie</option>
                                                    <option value="Developer" >Developer</option>
                                                    <option value="Menage" >Menage</option>
                                                    <option value="Menuisier" >Menuisier</option>
                                                    <option value="Menuisier" >Menuisier</option>
                                                    <option value="Jardinage" >Jardinage</option>
                                                    <option value="Electricien" >Electricien</option>
                                                    </select>
                                                    
                                                </div>
                                            </div>
                                            <div className="col-6">
                                                <label for="exampleFormControlTextarea1">Min Wage / per hour</label>
                                                <input className="form-control" type="number" onChange={this.handleOfferChange} name="minimumWage"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div className="row">
                <div className="table">
                        <div className="titleDashboard">My Offers</div>
                        <table className="table">
                            <thead>
                                <tr>
                                <th scope="col">ID </th>
                                <th scope="col">Description</th>
                                <th scope="col">Activity</th>
                                <th scope="col">City</th>
                                <th scope="col">Minimum Wage</th>
                                <th scope="col">Start Day</th>
                                </tr>
                            </thead>
                            <tbody>
                                {offers()}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    }
};

export default  withRouter(CustomerDashboard) 