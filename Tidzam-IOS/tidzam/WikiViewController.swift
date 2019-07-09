//
//  WikiViewController.swift
//  tidzam
//
//  Created by Lachihab Sabri on 28/01/2018.
//  Copyright © 2018 Lachihab Sabri. All rights reserved.
//

import UIKit


class WikiViewController: UIViewController, UINavigationBarDelegate {
    
    var urlwiki : String = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="


    @IBOutlet weak var imagespecie: UIImageView!
    
    @IBOutlet weak var introTxtview: UITextView!
    
    @IBOutlet weak var specieLbl: UILabel!
    
    
    
    var specie : String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print(specie)
        specieLbl.text=specie
        introTxtview.isEditable=false
        imagespecie.image = UIImage(named: specie)
        extractIntroWiki()
    }
    
    
    func extractIntroWiki()
    {
        var url : String = ""
        if(specieLbl.text=="crickets")
        {
            url = urlwiki + "cricket"
        }
        else if(specieLbl.text=="red_winged_blackbird")
        {
            url = urlwiki + "Red-winged_blackbird"
        }
        
        else
        {
            url = urlwiki + specieLbl.text!
        }
        let request = NSMutableURLRequest(url: URL(string: url)!)
        request.httpMethod = "GET"
        let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
        {
            data, response, error in
            if (error != nil) {
                print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
            }
            let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
            if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
            {
                if let items = jsonObj!.value(forKey: "query") as? NSDictionary
                {
                    if let items_ = items.value(forKey: "pages") as? NSDictionary
                    {
                        if(items_.makeIterator().next() != nil)
                        {
                            if let intro = items_.makeIterator().next()?.value as? NSDictionary
                            {
                                if let extract = intro.value(forKey: "extract") as? String
                                {
                                    DispatchQueue.main.async(execute: {
                                    self.introTxtview.text=extract
                                    })
                                }
                            }
                        }
                    }
                }
            }
                
            if error == nil {
                // Ce que vous voulez faire.
            }
        }
        requestAPI.resume()
    }
}
