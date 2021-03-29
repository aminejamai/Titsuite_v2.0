import {React, Component } from "react"
import HorizontalNav from "./HorizontalNav"
import customerHistorique from './images/customerHistorique.svg'

import {BsCalendar, BsStarFill} from 'react-icons/bs'
import {IoMdLocate, IoIosNotifications} from 'react-icons/io'
import {FaMoneyBillWave, FaRegComments} from 'react-icons/fa'
import {RiFolderUserLine} from 'react-icons/ri'
import {MdDescription} from 'react-icons/md'

export default class CustomerHistorique extends Component{

    state ={
        items : []
    }

    componentDidMount() {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/myjobs/completed', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            this.setState(
                {items : data}
            )
        });
    }
    
    render(){
        const offers = ()=>{
            return this.state.items.map((item) => {
                return (<div className="serviceItem">
                    <h6><MdDescription/> {item.description} </h6> 
                    <hr/>
                    <RiFolderUserLine /> Mr. {item.last_name} {item.first_name} <br/>
                    <IoMdLocate /> {item.city} <br/>
                    <FaMoneyBillWave /> {item.minimumWage} <br/>
                    <BsCalendar /> {item.period} <br/>
                    <FaRegComments /> {item.review} <br/> <BsStarFill /> {item.rate}
                </div>
                )
            })
        }
        return(
            <div className="row">
                <div className="col-2">
                    <HorizontalNav logout={this.props.rest.logout}/>
                </div>
                <div className="col-5 Content">
                   {offers()}
                </div>
                <div className="col-4 profileIllustration">
                    <img src={customerHistorique} className="img-fluid" alt="Logo" />
                </div>
            </div>
        )
        
    }
}