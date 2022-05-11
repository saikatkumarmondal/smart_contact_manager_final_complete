console.log("Om Namah Shivaya");
const toggleSidebar=()=>{
	if($('.sidebar').is(':visible')){
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}
function search(){
    let query=$('#search-input').val();
    let url=`http://localhost:8080/search/${query}`
    if(query==''){
        $('.search-result').hide();
    }else{
fetch(url).then((response)=>{
    return response.json();
}).then((data)=>{
    data.forEach((contacts)=>{
        let text=`<div class='list-group'>`
        text+=`<a href='/user/${contacts.cId}/contact' class='list-group-item list-group-item-action'>${contacts.name}</a>`
        text+=`</div>`
        $('.search-result').html(text);
        $('.search-result').show();
    })
})
    }
};
//first request to server to create order
const paymentStarted=()=>{
   let amount = $('#payment-field').val();
   if(amount==null || amount == ''){
       alert("amount is required..!!");
       return;
   }
   //we will use ajax to send request to server to create order
   $.ajax(
       {
           url:'/user/create_order',
           data:JSON.stringify({amount:amount,info:'order_request'}),
           contentType:'application/json',
           type:'POST',
           dataType:'json',
           success:function(response) {
               //invoked success
               console.log(response);
               if(response.status == 'created'){
                   //open payment form
                   let options={
                       key:'rzp_test_RMxk3b9Szit29G',
                       amount:response.amount,
                       currency:'INR',
                       name:'Smart Contact Manager',
                       description:'Donation',
                      image:'https://www.creativechameleonstudio.com/wp-content/uploads/2020/04/New-Blog-Headers_How-Much-Logo-Price.png',
                    order_id:response.id,
                    handler:function(response){
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.rozarpay_signature);
                        swal("Successfull!", "payment successfull", "success");
                       
                    },
                    prefill:{
                        name:'',
                        email:'',
                        contact:''
                    },
                    notes:{
                        address:'saikat kumar mondal'
                    },
                    theme:{
                        'color':'#3399cc'
                    },


                   };
                   let rzp=new Razorpay(options);
                   rzp.on('payment.failed',function(response){
                    console.log(response.error.code);
                    console.log(response.error.description)
                    console.log(response.error.source)
                    console.log(response.error.step)
                    console.log(response.error.reason)
                    console.log(response.error.metadata.order_id)
                    console.log(response.error.metadata.payment_id)
                    swal("Faild...!", "Oops...Payment faild!", "success");
                    alert('');
                });
                   rzp.open();
               }
           },
           error:function(error){
    console.log(error);
    alert('something went wrong..');
           }
       }
   )
}
    