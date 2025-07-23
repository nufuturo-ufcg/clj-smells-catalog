(ns examples.traditional.smells.long-parameter-list)

(defn register-new-user
  [username password email phone age gender location interests newsletter-opt-in referred-by]
  {:username username
   :password password
   :email email
   :phone phone
   :age age
   :gender gender
   :location location
   :interests interests
   :newsletter newsletter-opt-in
   :referral referred-by})

(println
 (register-new-user
  "alice123"              
  "securepass"            
  "alice@example.com"     
  "555-1234"              
  30                      
  "female"                
  "123 Main St"           
  ["reading" "hiking"]    
  true                    
  "referral-code-xyz"))   
