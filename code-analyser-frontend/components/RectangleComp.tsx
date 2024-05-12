'use client'

import Link from "next/link"
import { useState, useEffect } from "react"

import { FaPlus } from "react-icons/fa";
import ContentNode from "./Node";
import { Badge, Label } from "flowbite-react";
import { Review } from "./NodeDetails";

interface RectangleCompProps {
   score: number,
   code: string,
   fileName: string,
   results: Review[]
}

export default function RectangleComp({score, code, fileName, results} : RectangleCompProps) {

    const [IsOpen, setIsOpen] = useState(false)

    const toggle = () => {
        setIsOpen(!IsOpen);
      };

    return (
        <>
            <div className="flex flex-row w-full h-full h-20">
                <div className="flex flex-row rounded-full grow rounded-l-lg bg-black font-bold text-center justify-center">
                        <Label className="flex-auto content-center w-64 h-16 text-white text-left px-10">{fileName}</Label>
                        <div className="flex-none content-center">
                            <Badge className={`font-bold content-center text-black flex-none justify-items-center text-center px-10`} color={score < 50 ? 
                                'failure' : score < 80 ? 
                                'warning' : 'success'}>{score}/100
                            </Badge>
                        </div>
                        <button className="flex-none w-16 h-16 px-10" onClick={toggle}>
                            <FaPlus />
                        </button>
                </div>
                </div>
                {IsOpen &&
                <div className="animate-in bg-black">
                <ContentNode reviews={results} score={score} code={code}/>
            </div>}
        </>

            /*<div className="flex flex-row w-full h-20">
                <div className="rounded-l-full px-10 grow flex rounded-l-lg bg-black font-bold items-center text-center ">{fileName}</div>
                <div className={`px-10 flex w-2/12 font-bold text-black text-center items-center justify-center ${
                    score < 50 ? 
                    'bg-red-600' : score < 80 ? 
                    'bg-yellow-300' : 'bg-lime-400'
                }`}> {score}/100</div>
                <div className="flex justify-center w-1/12 items-center bg-emerald-500 text-black border-gray rounded-r-full">
                    <button onClick={toggle}>
                        <FaPlus />
                    </button>
                </div>
            </div>
            {IsOpen &&
            <div className="animate-in bg-black">
            <ContentNode />
            </div>}
            </>*/
    )
}